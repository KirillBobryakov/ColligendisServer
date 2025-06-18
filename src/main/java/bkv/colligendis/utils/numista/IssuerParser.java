package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.service.UniqueEntityException;
import bkv.colligendis.database.service.numista.IssuerService;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import java.util.Map;

public class IssuerParser extends NumistaPartParser {

    public IssuerParser() {
        super((page, nType) -> {
            Map<String, String> emetteur = getAttributeWithTextSingleOption(page, "#emetteur", "value");

            if (emetteur == null) {
                DebugUtil.showError(IssuerParser.class, "Can't find Issuer on the page nid: " + nType.getNid());
                return ParseEvent.ERROR;
            }

            String code = emetteur.get("value");
            String name = emetteur.get("text");

            if (nType.getIssuer() != null && nType.getIssuer().getCode() != null && nType.getIssuer().getCode().equals(code) && nType.getIssuer().getName().equals(name)) {
                DebugUtil.showInfo(IssuerParser.class, "The Issuer for nType is equal to Issuer on the page.");
                return ParseEvent.NOT_CHANGED;
            }

            Issuer issuer = null;
            try {
                issuer = N4JUtil.getInstance().numistaService.issuerService.findIssuerByCodeOrName(code, name);
            } catch (UniqueEntityException e) {
                DebugUtil.showError(IssuerService.class, "Find more then 1 Issuer by name: " + e.getParams().get("name") + "or code: " + e.getParams().get("code"));
            }

            if (issuer == null) {  //create new Issuer with code and name
                issuer = N4JUtil.getInstance().numistaService.issuerService.save(new Issuer(code, name));
                DebugUtil.showInfo(IssuerParser.class, "Create new Issuer with (code, name) = (" + code + ", " + name + ")");
            }

            if (issuer.getCode() == null || issuer.getCode().isEmpty() || !issuer.getCode().equals(code)) {
                DebugUtil.showWarning(IssuerParser.class, "For Issuer: " + name + " change a code from (" + issuer.getCode() + ") to (" + code + ")");

                issuer.setCode(code);
                issuer = N4JUtil.getInstance().numistaService.issuerService.save(issuer);
            }

            if (!issuer.getName().equals(name)) {
                DebugUtil.showWarning(IssuerParser.class, "For Issuer: " + code + " change a name from (" + issuer.getName() + ") to (" + name + ")");

                issuer.setName(name);
                issuer = N4JUtil.getInstance().numistaService.issuerService.save(issuer);
            }

            nType.setIssuer(issuer);
            DebugUtil.showInfo(IssuerParser.class, "Set an Issuer with (code, name) = (" + issuer.getCode() + ", " + issuer.getName() + ")");





            return ParseEvent.CHANGED;
        });

        this.partName = "Issuer";
    }
}
