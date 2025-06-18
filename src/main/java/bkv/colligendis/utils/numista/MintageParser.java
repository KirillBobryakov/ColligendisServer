package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.database.entity.numista.*;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MintageParser extends NumistaPartParser {


    public MintageParser() {
        super((page, nType) -> {
            ParseEvent result = ParseEvent.NOT_CHANGED;

            Element anneesElement = page.selectFirst("#annees");
            if (anneesElement == null) {
                DebugUtil.showError(MintageParser.class, "Can't find 'annees' while parsing page with nid: " + nType.getNid());
                return ParseEvent.ERROR;
            }


            HashMap<String, String> calendrier = getAttributeWithTextSelectedOption(anneesElement, "#calendrier");
            if (calendrier == null) {
                DebugUtil.showError(MintageParser.class, "Can't find Calendar while parsing page with nid: " + nType.getNid());
                return ParseEvent.ERROR;
            }

            Calendar calendar = N4JUtil.getInstance().numistaService.calendarService.findByCode(calendrier.get("value"), calendrier.get("text"));

            assert calendar != null;

            Elements trElements = anneesElement.select("tr");

            for (Element trElement : trElements) {
                Elements tdElements = trElement.select("td");

                Variant variant = null;

                String variantNid = null;
                if (!tdElements.isEmpty() && !tdElements.get(0).children().isEmpty()) {
                    variantNid = tdElements.get(0).children().get(0).attr("name");
                    if (variantNid.startsWith("nd")) {
                        variantNid = variantNid.substring(2);
                        final String varNid = variantNid;

                        variant = nType.getVariants().stream().filter(v -> v.getNid().equals(varNid)).findFirst().orElse(null);
                        if (variant == null) {
                            variant = N4JUtil.getInstance().numistaService.variantService.findByNid(variantNid);
                            nType.getVariants().add(variant);
                        }
                    } else if(variantNid.startsWith("nouveau")){
                        continue;
                    } else {
                        DebugUtil.showWarning(MintageParser.class, "Find string with not 'nd' and 'nouveau' prefix while parsing page with nid: " + nType.getNid());
                        continue;
                    }
                } else {
                    continue;
                }
                if (variant == null && !variantNid.isEmpty()) {
                    variant = new Variant(variantNid);
                    nType.getVariants().add(variant);
                    DebugUtil.showInfo(MintageParser.class, "Add new variants for NType with nid: " + nType.getNid());
                }

                assert variant != null;

                //Fill variant
                for (Element tdElement : tdElements) {
                    switch (tdElement.attr("class")) {
                        case "date_check"-> {

                            Element dateCheckElement = tdElement.selectFirst("input");
                            if (dateCheckElement != null && dateCheckElement.attr("checked").equals("checked")) {
                                String name = dateCheckElement.attr("name").substring(2);

                                Element datesElement = page.selectFirst("#dates" + name);
                                if (datesElement == null) {
                                    DebugUtil.showWarning(MintageParser.class, "The date_check is checked but can't find Element with id=#dates" + name + " while parsing page with nid: " + nType.getNid());
                                    continue;
                                }
                                Elements inputDatesElements = datesElement.select("input");
                                if (inputDatesElements.size() == 2) {
                                    String dateFrom = inputDatesElements.get(0).attr("value");
                                    String dateTill = inputDatesElements.get(1).attr("value");

                                    variant.setYear(null);
                                    if (StringUtils.isNumeric(dateFrom)) {
                                        Year yearFrom = N4JUtil.getInstance().numistaService.yearService.findYearByValueAndCalendarCode(Integer.valueOf(dateFrom), Calendar.GREGORIAN_CODE);
                                        variant.setYearFrom(yearFrom);
                                    }
                                    if (StringUtils.isNumeric(dateTill)) {
                                        Year yearTill = N4JUtil.getInstance().numistaService.yearService.findYearByValueAndCalendarCode(Integer.valueOf(dateTill), Calendar.GREGORIAN_CODE);
                                        variant.setYearTill(yearTill);
                                    }
                                }
                            }
                        }
                        case "date_year" -> {
                            Element millesimeElement = tdElement.selectFirst("input");
                            if (millesimeElement == null) {
                                DebugUtil.showWarning(MintageParser.class, "Can't find 'millesime' input while parsing page with nid: " + nType.getNid());
                                continue;
                            }
                            if (millesimeElement.attr("value").isEmpty()) {
                                DebugUtil.showInfo(MintageParser.class, "Value of 'millesime' is empty input while parsing page with nid: " + nType.getNid());
                                continue;
                            }

                            if (millesimeElement.attr("name").startsWith("millesime")) {
                                Integer yearValue = Integer.valueOf(millesimeElement.attr("value"));

                                if (variant.getYear() != null && Objects.equals(variant.getYear().getValue(), yearValue) && variant.getYear().getCalendar().equals(calendar)) {
                                    continue;
                                }

                                Year year = N4JUtil.getInstance().numistaService.yearService.findYearByValueAndCalendarCode(yearValue, calendar.getCode());
                                if (year == null) {
                                    year = N4JUtil.getInstance().numistaService.yearService.save(new Year(yearValue, calendar));
                                }
                                variant.setYear(year);
                            }
                        }
                        case "date_mint" -> {
                            Element atelierElement = tdElement.selectFirst("input");
                            if (atelierElement == null) {
                                DebugUtil.showWarning(MintageParser.class, "Can't find 'atelier' input while parsing page with nid: " + nType.getNid());
                                continue;
                            }
                            if (atelierElement.attr("name").startsWith("atelier") && !atelierElement.attr("value").isEmpty()) {
                                variant.setMintLetter(atelierElement.attr("value"));
                            }
                        }
                        case "date_mark" -> {
                            Elements selectOptions = tdElement.select("option");
                            ArrayList<String> marks = new ArrayList<>();
                            for (Element optionElement : selectOptions) {
                                marks.add(optionElement.attr("value"));
                            }

                            if(!variant.getMarks().isEmpty()){
                                int countMarks = variant.getMarks().size();
                                variant.getMarks().removeIf(mark -> marks.stream().noneMatch(s -> mark.getNid().equals(s)));
                                if (variant.getMarks().size() != countMarks) {
                                    result = ParseEvent.CHANGED;
                                    DebugUtil.showInfo(MintageParser.class, "Find stale marks in variant with nid=" + variant.getNid());
                                }
                            }

                            for (String markNid : marks) {
                                if (variant.getMarks().stream().noneMatch(s -> s.getNid().equals(markNid))) {
                                    Mark mark = N4JUtil.getInstance().numistaService.markService.findByNid(markNid);
                                    if (mark != null) {
                                        mark = new Mark(markNid);
                                    }
                                    variant.getMarks().add(mark);
                                    DebugUtil.showInfo(MintageParser.class, "Add new mark for variant with nid=" + variant.getNid());
                                    result = ParseEvent.CHANGED;
                                }
                            }
                        }
                        case "date_mintage" -> {
                            Element input = tdElement.selectFirst("input");
                            if (input != null) {
                                String mintage_str = getAttribute(input, "value");
                                if (mintage_str != null) {
                                    int mintage = Integer.parseInt(mintage_str.replaceAll(" ", ""));
                                    if (variant.getMintage() != mintage) {
                                        variant.setMintage(mintage);
                                        DebugUtil.showInfo(MintageParser.class, "Set new mintage for variant with nid=" + variant.getNid());
                                        result = ParseEvent.CHANGED;
                                    }
                                }
                            }
                        }
                        case "" -> {
/*
<div class="reference">
    <select style="width: 6em;" name="first_ref290" class="catalogue_select" data-edit="1">
        <option value="109">Gad 1789</option>
    </select>
    <span style="width: 1em">#</span>
    <input class="first_version_reference" type="text" name="first_number290" value="775">
    <span style="width: 1em;"></span>
</div>

 <div id="second_reference290" class="reference" style="display:flex;">
    <select style="width: 6em;" name="second_ref290" class="catalogue_select" data-edit="1">
        <option value="34">Franc 2014</option>
    </select>
    <span style="width: 1em">#</span>
    <input class="second_version_reference" type="text" name="second_number290" value="344/2">
    <span style="width: 1em"></span>
 </div>
 */
                            List<ReferenceToCatalogue> references = new ArrayList<>();

                            Elements divs = tdElement.select("div");
                            for (Element divElement : divs) {
                                Element optionElement = divElement.selectFirst("option");
                                Element inputElement = divElement.selectFirst("input");
                                if (optionElement != null && inputElement != null) {
                                    references.add(new ReferenceToCatalogue(
                                            optionElement.attr("value"),
                                            optionElement.text(),
                                            inputElement.attr("value")));
                                }
                            }

                            if(variant.getCatalogueReferences() != null){
                                int countReferences = variant.getCatalogueReferences().size();
                                // Find stale Reference in variant
                                variant.getCatalogueReferences().removeIf(catalogueReference -> references.stream()
                                        .noneMatch(reference -> reference.getCatalogueNid().equals(catalogueReference.getCatalogue().getNid())
                                                && reference.getNumber().equals(catalogueReference.getNumber())));

                                if (variant.getCatalogueReferences().size() != countReferences) {
                                    DebugUtil.showWarning(MintageParser.class, "Find stale references for variant with nid=" + variant.getNid());
                                    result = ParseEvent.CHANGED;
                                }
                            }

                            for (ReferenceToCatalogue reference : references) {
                                if (variant.getCatalogueReferences().stream().noneMatch(ref -> ref.getCatalogue().getNid().equals(reference.getCatalogueNid())
                                        && ref.getNumber().equals(reference.getNumber()))) {

                                    Catalogue catalogue = N4JUtil.getInstance().numistaService.catalogueService.findByNid(reference.catalogueNid, reference.catalogueCode);
                                    if (catalogue == null) {
                                        ReferenceNumberParser.parseReferenceCataloguesByCode(reference.catalogueCode);
                                        catalogue = N4JUtil.getInstance().numistaService.catalogueService.findByNid(reference.catalogueNid, reference.catalogueCode);

                                        if (catalogue == null) {
                                            DebugUtil.showError(MintageParser.class, "Error with find or create new Catalogue with nid=" + reference.catalogueNid + " and code=" + reference.catalogueCode + ".");
                                            return ParseEvent.ERROR;
                                        }
                                    }


                                    CatalogueReference catalogueReference = N4JUtil.getInstance().numistaService.catalogueReferenceService.findByNumberAndCatalogueNid(reference.number, catalogue);
                                    if (catalogueReference == null) {
                                        DebugUtil.showError(MintageParser.class, "New CatalogueReference with nid=" + reference.catalogueNid + " and code=" + reference.catalogueCode + " and number=" + reference.number + " can't be created.");
                                        return ParseEvent.ERROR;
                                    }

                                    variant.getCatalogueReferences().add(catalogueReference);
                                    DebugUtil.showInfo(MintageParser.class, "Add new CatalogueReference with number=" + catalogueReference.getNumber() + " and catalogue code=" + catalogueReference.getCatalogue().getCode() + " and catalogue nid=" + catalogueReference.getCatalogue().getNid() + "");
                                    result = ParseEvent.CHANGED;
                                }

                            }
                        }

                        case "yearline_comment" -> {

                            String comment = getAttribute(tdElement.selectFirst("input"), "value");
                            if(comment != null) {
                                if(variant.getComment() != null && !variant.getComment().equals(comment)){
                                    DebugUtil.showInfo(MintageParser.class, "Change comment to variant with nid=" + variant.getNid());
                                } else if(variant.getComment() == null){
                                    DebugUtil.showInfo(MintageParser.class, "Add comment to variant with nid=" + variant.getNid());
                                    variant.setComment(comment);
                                    result = ParseEvent.CHANGED;
                                }
                            }

                        }

                        default -> {
                        }
                    }
                }
            }

            return result;
        });

        this.partName = "Mintage";
    }
}