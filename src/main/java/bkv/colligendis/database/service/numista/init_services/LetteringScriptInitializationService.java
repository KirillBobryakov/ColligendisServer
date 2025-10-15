package bkv.colligendis.database.service.numista.init_services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import bkv.colligendis.database.entity.numista.LetteringScript;
import bkv.colligendis.database.service.numista.LetteringScriptService;
import lombok.AllArgsConstructor;
import lombok.Data;

@Service
public class LetteringScriptInitializationService {
    private static final Logger logger = LogManager.getLogger(LetteringScriptInitializationService.class);

    private final LetteringScriptService letteringScriptService;

    public LetteringScriptInitializationService(LetteringScriptService letteringScriptService) {
        this.letteringScriptService = letteringScriptService;
    }

    public void initializeAllLetteringScripts() {
        logger.info("Initializing lettering scripts...");
        List<LetteringScriptData> letteringScripts = getAllLetteringScripts();
        letteringScripts.forEach(letteringScriptData -> {
            UUID letteringScriptUuid = letteringScriptService.findUuidByNid(letteringScriptData.getNid());
            if (letteringScriptUuid == null) {
                letteringScriptUuid = letteringScriptService
                        .save(new LetteringScript(letteringScriptData.getNid(), letteringScriptData.getName()))
                        .getUuid();
            } else {
                if (!letteringScriptService.compareName(letteringScriptUuid, letteringScriptData.getName())) {
                    letteringScriptService.setName(letteringScriptUuid, letteringScriptData.getName());
                }
            }
        });
        logger.info("Initialized " + letteringScripts.size() + " lettering scripts.");
    }

    @Data
    @AllArgsConstructor
    private static class LetteringScriptData {
        private final String nid;
        private final String name;
    }

    private List<LetteringScriptData> getAllLetteringScripts() {
        List<LetteringScriptData> letteringScripts = new ArrayList<>();

        // Initialize all lettering scripts from Numista
        letteringScripts.add(new LetteringScriptData("1", "'Phags-pa"));
        letteringScripts.add(new LetteringScriptData("2", "Ancient South Arabian"));
        letteringScripts.add(new LetteringScriptData("3", "Arabic"));
        letteringScripts.add(new LetteringScriptData("4", "Arabic (kufic)"));
        letteringScripts.add(new LetteringScriptData("93", "Arabic (Maghribi)"));
        letteringScripts.add(new LetteringScriptData("95", "Arabic (naskh)"));
        letteringScripts.add(new LetteringScriptData("92", "Arabic (ruqʿah)"));
        letteringScripts.add(new LetteringScriptData("110", "Arabic (thuluth)"));
        letteringScripts.add(new LetteringScriptData("94", "Arabic (tughra)"));
        letteringScripts.add(new LetteringScriptData("5", "Aramaic"));
        letteringScripts.add(new LetteringScriptData("6", "Armenian"));
        letteringScripts.add(new LetteringScriptData("96", "Aurebesh"));
        letteringScripts.add(new LetteringScriptData("105", "Autobot"));
        letteringScripts.add(new LetteringScriptData("70", "Baybayin"));
        letteringScripts.add(new LetteringScriptData("7", "Bengali"));
        letteringScripts.add(new LetteringScriptData("9", "Brahmi"));
        letteringScripts.add(new LetteringScriptData("10", "Braille"));
        letteringScripts.add(new LetteringScriptData("8", "Burmese"));
        letteringScripts.add(new LetteringScriptData("114", "Carian"));
        letteringScripts.add(new LetteringScriptData("73", "Cherokee"));
        letteringScripts.add(new LetteringScriptData("11", "Chinese"));
        letteringScripts.add(new LetteringScriptData("19", "Chinese (simplified)"));
        letteringScripts.add(new LetteringScriptData("12", "Chinese (traditional, clerical script)"));
        letteringScripts.add(new LetteringScriptData("13", "Chinese (traditional, Dai script)"));
        letteringScripts.add(new LetteringScriptData("14", "Chinese (traditional, grass script)"));
        letteringScripts.add(new LetteringScriptData("15", "Chinese (traditional, regular script)"));
        letteringScripts.add(new LetteringScriptData("16", "Chinese (traditional, running script)"));
        letteringScripts.add(new LetteringScriptData("17", "Chinese (traditional, seal script)"));
        letteringScripts.add(new LetteringScriptData("18", "Chinese (traditional, slender gold script)"));
        letteringScripts.add(new LetteringScriptData("101", "Cuneiform"));
        letteringScripts.add(new LetteringScriptData("20", "Cypriot"));
        letteringScripts.add(new LetteringScriptData("21", "Cyrillic"));
        letteringScripts.add(new LetteringScriptData("22", "Cyrillic (cursive)"));
        letteringScripts.add(new LetteringScriptData("112", "Cyrillic (medieval)"));
        letteringScripts.add(new LetteringScriptData("99", "Demotic"));
        letteringScripts.add(new LetteringScriptData("103", "Deseret"));
        letteringScripts.add(new LetteringScriptData("23", "Devanagari"));
        letteringScripts.add(new LetteringScriptData("102", "Etruscan"));
        letteringScripts.add(new LetteringScriptData("25", "Ge'ez"));
        letteringScripts.add(new LetteringScriptData("85", "Georgian (Asomtavruli)"));
        letteringScripts.add(new LetteringScriptData("26", "Georgian (Mkhedruli)"));
        letteringScripts.add(new LetteringScriptData("86", "Georgian (Nuskhuri)"));
        letteringScripts.add(new LetteringScriptData("27", "Glagolitic"));
        letteringScripts.add(new LetteringScriptData("28", "Greek"));
        letteringScripts.add(new LetteringScriptData("91", "Greek (retrograde)"));
        letteringScripts.add(new LetteringScriptData("29", "Gujarati"));
        letteringScripts.add(new LetteringScriptData("30", "Gurmukhi"));
        letteringScripts.add(new LetteringScriptData("31", "Hangul"));
        letteringScripts.add(new LetteringScriptData("32", "Hebrew"));
        letteringScripts.add(new LetteringScriptData("34", "Hieroglyphic"));
        letteringScripts.add(new LetteringScriptData("67", "Hiragana"));
        letteringScripts.add(new LetteringScriptData("113", "Hiragana (hentaigana)"));
        letteringScripts.add(new LetteringScriptData("81", "Iberian"));
        letteringScripts.add(new LetteringScriptData("77", "Iberian (Celtiberian)"));
        letteringScripts.add(new LetteringScriptData("78", "Iberian (Levantine)"));
        letteringScripts.add(new LetteringScriptData("79", "Iberian (Meridional)"));
        letteringScripts.add(new LetteringScriptData("80", "Iberian (South-Lusitanian)"));
        letteringScripts.add(new LetteringScriptData("50", "Inscriptional Pahlavi"));
        letteringScripts.add(new LetteringScriptData("68", "Inuktitut"));
        letteringScripts.add(new LetteringScriptData("35", "Javanese"));
        letteringScripts.add(new LetteringScriptData("111", "Jawi"));
        letteringScripts.add(new LetteringScriptData("36", "Kannada"));
        letteringScripts.add(new LetteringScriptData("65", "Katakana"));
        letteringScripts.add(new LetteringScriptData("106", "Kawi"));
        letteringScripts.add(new LetteringScriptData("37", "Kharosthi"));
        letteringScripts.add(new LetteringScriptData("76", "Khitan large script"));
        letteringScripts.add(new LetteringScriptData("75", "Khitan small script"));
        letteringScripts.add(new LetteringScriptData("38", "Khmer"));
        letteringScripts.add(new LetteringScriptData("39", "Lao"));
        letteringScripts.add(new LetteringScriptData("40", "Latin"));
        letteringScripts.add(new LetteringScriptData("43", "Latin (cursive)"));
        letteringScripts.add(new LetteringScriptData("89", "Latin (Fraktur blackletter)"));
        letteringScripts.add(new LetteringScriptData("24", "Latin (Gaelic)"));
        letteringScripts.add(new LetteringScriptData("90", "Latin (retrograde)"));
        letteringScripts.add(new LetteringScriptData("41", "Latin (uncial)"));
        letteringScripts.add(new LetteringScriptData("107", "Lontara"));
        letteringScripts.add(new LetteringScriptData("64", "Lycian"));
        letteringScripts.add(new LetteringScriptData("44", "Malayalam"));
        letteringScripts.add(new LetteringScriptData("108", "Maya"));
        letteringScripts.add(new LetteringScriptData("62", "Mongolian (folded)"));
        letteringScripts.add(new LetteringScriptData("45", "Mongolian / Manchu"));
        letteringScripts.add(new LetteringScriptData("100", "Morse"));
        letteringScripts.add(new LetteringScriptData("82", "Nabataean"));
        letteringScripts.add(new LetteringScriptData("46", "Neo-Punic"));
        letteringScripts.add(new LetteringScriptData("63", "Odia"));
        letteringScripts.add(new LetteringScriptData("47", "Old Italics"));
        letteringScripts.add(new LetteringScriptData("48", "Old Turkic"));
        letteringScripts.add(new LetteringScriptData("49", "Old Uyghur"));
        letteringScripts.add(new LetteringScriptData("109", "Osage"));
        letteringScripts.add(new LetteringScriptData("71", "Persian"));
        letteringScripts.add(new LetteringScriptData("88", "Persian (nastaliq)"));
        letteringScripts.add(new LetteringScriptData("51", "Phoenician"));
        letteringScripts.add(new LetteringScriptData("87", "Psalter Pahlavi"));
        letteringScripts.add(new LetteringScriptData("52", "Ranjana"));
        letteringScripts.add(new LetteringScriptData("98", "Rongorongo"));
        letteringScripts.add(new LetteringScriptData("53", "Runic"));
        letteringScripts.add(new LetteringScriptData("84", "Cirth"));
        letteringScripts.add(new LetteringScriptData("83", "Elder Futhark"));
        letteringScripts.add(new LetteringScriptData("104", "Sharada"));
        letteringScripts.add(new LetteringScriptData("54", "Sinhala"));
        letteringScripts.add(new LetteringScriptData("55", "Sogdian"));
        letteringScripts.add(new LetteringScriptData("56", "Syriac"));
        letteringScripts.add(new LetteringScriptData("57", "Tamil"));
        letteringScripts.add(new LetteringScriptData("58", "Tangut"));
        letteringScripts.add(new LetteringScriptData("59", "Telugu"));
        letteringScripts.add(new LetteringScriptData("74", "Tengwar"));
        letteringScripts.add(new LetteringScriptData("66", "Thaana"));
        letteringScripts.add(new LetteringScriptData("60", "Thai"));
        letteringScripts.add(new LetteringScriptData("61", "Tibetan"));
        letteringScripts.add(new LetteringScriptData("97", "Tifinagh"));
        letteringScripts.add(new LetteringScriptData("72", "Urdu"));

        return letteringScripts;
    }
}
