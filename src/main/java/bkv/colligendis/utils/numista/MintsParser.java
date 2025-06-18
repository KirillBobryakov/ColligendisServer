package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.*;
import bkv.colligendis.utils.N4JUtil;
import org.jsoup.nodes.Element;

import java.util.HashMap;

public class MintsParser extends NumistaPartParser {

    public MintsParser() {
        super((page, nType) -> {
            ParseEvent result = ParseEvent.NOT_CHANGED;

            Element mints = page.selectFirst("fieldset:contains(Mint(s))");

            if (mints == null) return ParseEvent.NOT_CHANGED;

            nType.getSpecifiedMints().clear();

            int i = 0;
            while (true) {
                Element mintIdentifierElement = mints.selectFirst("input[name=mint_identifier" + i + "]");
                Element mintElement = mints.selectFirst("select[name=mint" + i + "]");
                Element mintmarkElement = mints.selectFirst("select[name=mintmark" + i + "]");
                if (mintIdentifierElement != null && mintElement != null && mintmarkElement != null && mintElement.selectFirst("option") != null) {

                    Mint mint = null;

                    HashMap<String, String> mintCode = getAttributeWithTextSingleOption(mintElement, "value");
                    if (isValueAndTextNotNullAndNotEmpty(mintCode)) {
                        mint = N4JUtil.getInstance().numistaService.mintService.findByNid(mintCode.get("value"), mintCode.get("text"));
                    }

                    if (mint != null) {

                        String mintmarkIdentifier = getAttribute(mintIdentifierElement, "value");

                        if (mintmarkIdentifier == null) {
                            mintmarkIdentifier = "";
                        }

                        Mintmark mintmark = null;
                        HashMap<String, String> mintmarkHashMap = getAttributeWithTextSingleOption(mintmarkElement, "value");

                        if (isValueAndTextNotNullAndNotEmpty(mintmarkHashMap)) {
                            mintmark = N4JUtil.getInstance().numistaService.mintmarkService.findByNid(mintmarkHashMap.get("value"));
                            if (mintmark != null && mintmark.getPicture() != null && !mintmark.getPicture().equals(mintmarkHashMap.get("text"))) {
                                mintmark.setPicture(mintmarkHashMap.get("text"));
                                N4JUtil.getInstance().numistaService.mintmarkService.save(mintmark);
                            }
                        }

                        SpecifiedMint specifiedMint = N4JUtil.getInstance().numistaService.specifiedMintService.findByIdentifierMintMintmark(mintmarkIdentifier, mint.getNid(), mintmark != null ? mintmark.getNid() : null);
                        if (specifiedMint == null) {
                            specifiedMint = new SpecifiedMint();
                            specifiedMint.setIdentifier(mintmarkIdentifier);
                            specifiedMint.setMint(mint);
                            specifiedMint.setMintmark(mintmark);
                            specifiedMint = N4JUtil.getInstance().numistaService.specifiedMintService.save(specifiedMint);
                        }


                        nType.getSpecifiedMints().add(specifiedMint);
                        result = ParseEvent.CHANGED;
                    }
                    i++;
                } else {
                    break;
                }
            }

            return result;
        });

        this.partName = "MintsParser";
    }


}
