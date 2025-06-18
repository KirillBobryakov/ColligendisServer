package bkv.colligendis.utils.numista.issuer;


import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Subject;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.numista.NumistaPartParser;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class NumistaAllIssuersParser {
    private static final String BASE_URL = "https://en.numista.com/catalogue/search_issuers.php";

    public static final String PATH_PREFIX = "/Users/kirillbobryakov/Coins/Numista/Issuers/";

    public NumistaAllIssuersParser() {
    }

    public List<Subject> fetchAndProcessCatalog() {
        List<Subject> rootSubjects = new ArrayList<>();

        ArrayList<NumistaIssuerResultItem> allIssuers = new ArrayList<>();
        NumistaIssuersResponse response = null;
        int page = 0;
        do{
            String url = String.format("%s?&p=%d&e=0&fl=0&ea=0&os=1", BASE_URL, page);

            String responseString = NumistaPartParser.fetchJson(url, true);
            try {
                Files.writeString(Paths.get(PATH_PREFIX + "response_" + page + ".txt"), responseString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            response = NumistaPartParser.fetchAndParseJson(url, true, NumistaIssuersResponse.class);

            page++;
        } while(response.getPagination().isMore());


        System.out.println(allIssuers.size());

        
        return rootSubjects;
    }



    public void processIssuersJsons2(){
        List<NumistaIssuerResultItem> allIssuers = new ArrayList<>();

        for(int i = 0; i <= 36; i++){
            String pathString = PATH_PREFIX + "response_" + i + ".txt";
            String jsonString = null;
            try {
                jsonString = Files.readString(Path.of(pathString));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(jsonString == null) continue;

            ObjectMapper objectMapper = new ObjectMapper();
            
            NumistaIssuersResponse response = null;
            try {
                response = objectMapper.readValue(jsonString, NumistaIssuersResponse.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if(response == null) continue;

            allIssuers.addAll(response.getResults());
        }

        processIssuersToTree(allIssuers);
    }

    public void processIssuersToTree(List<NumistaIssuerResultItem> allIssuers) {
        if (allIssuers == null || allIssuers.isEmpty()) return;

        // Stack to keep track of parent at each level
        List<ParentStackItem> parentStack = new ArrayList<>(); // Can hold Subject or Country
        NumistaIssuerResultItem previousIssuer = null;

        System.out.println("Processing " + allIssuers.size() + " issuers");

        for (int i = 0; i < allIssuers.size(); i++) {
            if (i % 100 == 0) {
                System.out.printf("Processing %d/%d (%.1f%% complete)%n", 
                    i, allIssuers.size(), (i * 100.0 / allIssuers.size()));
            }
            NumistaIssuerResultItem current = allIssuers.get(i);
            NumistaIssuerResultItem next = (i < allIssuers.size() - 1) ? allIssuers.get(i + 1) : null;

            if(!parentStack.isEmpty() && previousIssuer != null && current.getLevel() < previousIssuer.getLevel()){

                do{
                    parentStack.remove(parentStack.size() - 1);
                    if(!parentStack.isEmpty()){
                        previousIssuer = parentStack.get(parentStack.size() - 1).issuerResultItem;
                    }

                } while (!parentStack.isEmpty() && current.getLevel() <= previousIssuer.getLevel());
            }

            if (current.getLevel() == 1) { // Subject
                Subject subject = N4JUtil.getInstance().numistaService.subjectService.findSubjectByNumistaCode(current.getId());
                
                if (subject == null) {
                    subject = new Subject(current.getId(), current.getText());
                    N4JUtil.getInstance().numistaService.subjectService.save(subject);
                }
                parentStack.clear();
                parentStack.add(new ParentStackItem(subject, current));
            } else if (current.getLevel() == 2) { // Country
                Country country = N4JUtil.getInstance().numistaService.countryService.findCountryByNumistaCode(current.getId());
                if (country == null) {
                    country = new Country(current.getId(), current.getText());
                    country = N4JUtil.getInstance().numistaService.countryService.save(country);                }
                
                // Connect country to parent subject
                if (!parentStack.isEmpty() && parentStack.get(parentStack.size() - 1).object instanceof Subject) {
                    N4JUtil.getInstance().numistaService.countryService.connectToParentSubject(country.getUuid(), ((Subject) parentStack.get(parentStack.size() - 1).object).getUuid());

                }
                // Check if country has children
                boolean hasChildren = next != null && next.getLevel() > current.getLevel();
                if (!hasChildren) {
                    // Country is also an Issuer
                    Issuer issuer = N4JUtil.getInstance().numistaService.issuerService.findIssuerByNumistaCode(current.getId());
                    if (issuer == null) {
                        issuer = new Issuer(current.getId(), current.getText());
                        N4JUtil.getInstance().numistaService.issuerService.save(issuer);
                    }
                    // Connect issuer to country
                    N4JUtil.getInstance().numistaService.issuerService.connectToCountry(issuer.getUuid(), country.getUuid());
                }
                // Update stack
                if (hasChildren) {
                    parentStack.add(new ParentStackItem(country, current));
                }
            } else if (current.getLevel() > 2) {
                boolean hasChildren = next != null && next.getLevel() > current.getLevel();
                if (hasChildren) {
                    // Subject
                    Subject subject = N4JUtil.getInstance().numistaService.subjectService.findSubjectByNumistaCode(current.getId());
                    if (subject == null) {
                        subject = new Subject(current.getId(), current.getText());
                        N4JUtil.getInstance().numistaService.subjectService.save(subject);
                    }
                    // Set parent
                    if (!parentStack.isEmpty()) {
                        ParentStackItem parent = parentStack.get(parentStack.size() - 1);
                        if (parent.object instanceof Subject) {
                            N4JUtil.getInstance().numistaService.subjectService.connectToParentSubject(subject.getUuid(), ((Subject) parent.object).getUuid());
                        } else if (parent.object instanceof Country) {
                            N4JUtil.getInstance().numistaService.subjectService.connectToCountry(subject.getUuid(), ((Country) parent.object).getUuid());
                        }
                    }
                    parentStack.add(new ParentStackItem(subject, current));
                } else {
                    // Issuer
                    Issuer issuer = N4JUtil.getInstance().numistaService.issuerService.findIssuerByNumistaCode(current.getId());
                    if (issuer == null) {
                        issuer = new Issuer(current.getId(), current.getText());
                        N4JUtil.getInstance().numistaService.issuerService.save(issuer);
                    }
                    // Set parent
                    if (!parentStack.isEmpty()) {
                        ParentStackItem parent = parentStack.get(parentStack.size() - 1);
                        if (parent.object instanceof Subject) {
                            N4JUtil.getInstance().numistaService.issuerService.connectToParentSubject(issuer.getUuid(), ((Subject) parent.object).getUuid());
                        } else if (parent.object instanceof Country) {
                            N4JUtil.getInstance().numistaService.issuerService.connectToCountry(issuer.getUuid(), ((Country) parent.object).getUuid());
                        }
                    }
                }
            }
            previousIssuer = current;
        }
    }
} 

class ParentStackItem{
    public Object object;
    public NumistaIssuerResultItem issuerResultItem;

    public ParentStackItem(Object object, NumistaIssuerResultItem issuerResultItem) {
        this.object = object;
        this.issuerResultItem = issuerResultItem;
    }
}