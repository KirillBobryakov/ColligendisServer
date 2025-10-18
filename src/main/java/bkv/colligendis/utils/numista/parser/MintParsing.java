package bkv.colligendis.utils.numista.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;

import bkv.colligendis.database.entity.numista.Mint;
import bkv.colligendis.database.entity.numista.Mintmark;
import bkv.colligendis.database.entity.numista.SpecifiedMint;

public class MintParsing extends PartParser {

    private static final Logger logger = LogManager.getLogger(MintParsing.class);

    public MintParsing() {
        super((pageParser) -> {
            ParsingResult result = ParsingResult.NOT_CHANGED;

            Element mints = pageParser.getNumistaPage().selectFirst("fieldset:contains(Mint(s))");

            if (mints == null)
                return ParsingResult.NOT_CHANGED;

            List<UUID> specifiedMintsUuids = new ArrayList<>();

            int i = 0;
            while (true) {
                Element mintIdentifierElement = mints.selectFirst("input[name=mint_identifier" + i + "]");
                Element mintElement = mints.selectFirst("select[name=mint" + i + "]");
                Element mintmarkElement = mints.selectFirst("select[name=mintmark" + i + "]");

                if (mintIdentifierElement == null || mintElement == null || mintmarkElement == null
                        || mintElement.selectFirst("option") == null)
                    break;

                UUID mintUuid = null;

                HashMap<String, String> mintCode = getAttributeWithTextSingleOption(mintElement, "value");
                if (!isValueAndTextNotNullAndNotEmpty(mintCode))
                    continue;

                mintUuid = mintService.findUuidByNid(mintCode.get("value"));
                if (mintUuid == null) {
                    logger.error("Can't find Mint with nid: {} while parsing page with nid: {}",
                            mintCode.get("value"), pageParser.getNid());
                    return ParsingResult.ERROR;
                }

                String mintmarkIdentifier = getAttribute(mintIdentifierElement, "value");

                if (mintmarkIdentifier == null) {
                    mintmarkIdentifier = "";
                }

                HashMap<String, String> mintmarkHashMap = getAttributeWithTextSingleOption(mintmarkElement,
                        "value");

                if (!isValueAndTextNotNullAndNotEmpty(mintmarkHashMap))
                    continue;

                UUID mintmarkUuid = mintmarkService.findUuidByNid(mintmarkHashMap.get("value"));

                if (mintmarkUuid == null) {
                    mintmarkUuid = mintmarkService
                            .save(new Mintmark(mintmarkHashMap.get("value"), mintmarkHashMap.get("text"))).getUuid();
                }

                UUID specifiedMintUuid = specifiedMintService.findUuidByIdentifierMintMintmark(mintmarkIdentifier,
                        mintUuid,
                        mintmarkUuid);
                if (specifiedMintUuid == null) {
                    specifiedMintUuid = specifiedMintService.save(new SpecifiedMint(mintmarkIdentifier)).getUuid();

                    specifiedMintService.setSingleOutgoingRelationshipToNode(specifiedMintUuid, mintUuid,
                            SpecifiedMint.WITH_MINT, Mint.LABEL);
                    if (mintmarkUuid != null) {
                        specifiedMintService.setSingleOutgoingRelationshipToNode(specifiedMintUuid, mintmarkUuid,
                                SpecifiedMint.WITH_MINTMARK, Mintmark.LABEL);
                    }
                } else {
                    if (!specifiedMintService.compareMint(specifiedMintUuid, mintUuid)) {
                        specifiedMintService.setMint(specifiedMintUuid, mintUuid);
                    }
                    if (mintmarkUuid != null
                            && !specifiedMintService.compareMintmark(specifiedMintUuid, mintmarkUuid)) {
                        specifiedMintService.setMintmark(specifiedMintUuid, mintmarkUuid);
                    }
                }

                specifiedMintsUuids.add(specifiedMintUuid);
                i++;
            }

            if (specifiedMintsUuids.size() > 0) {
                if (nTypeService.equateSpecifiedMints(pageParser.getNTypeUuid(), specifiedMintsUuids)) {
                    result = ParsingResult.CHANGED;
                }
            }

            return result;
        });

        this.partName = "MintsParser (SpecifiedMint)";
    }

}
