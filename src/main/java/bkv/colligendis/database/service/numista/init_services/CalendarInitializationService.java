package bkv.colligendis.database.service.numista.init_services;

import bkv.colligendis.database.entity.numista.Calendar;
import bkv.colligendis.database.service.numista.CalendarService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service for initializing calendars in the database.
 * Provides methods for mass saving calendars from different sources.
 */
@Service
public class CalendarInitializationService {
    private static final Logger logger = LogManager.getLogger(CalendarInitializationService.class);

    private final CalendarService calendarService;

    public CalendarInitializationService(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    // Inner class for calendar data
    private static class CalendarData {
        private final String code;
        private final String name;
        private final Integer toGregorianShift;

        public CalendarData(String code, String name, Integer toGregorianShift) {
            this.code = code;
            this.name = name;
            this.toGregorianShift = toGregorianShift;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public Integer getToGregorianShift() {
            return toGregorianShift;
        }
    }

    public void initializeAllCalendars() {
        logger.info("Initializing calendars...");
        List<CalendarData> calendars = getAllCalendarData();
        calendars.forEach(calendarData -> {
            UUID calendarUuid = calendarService.findUuidByCode(calendarData.getCode());
            if (calendarUuid == null) {
                calendarUuid = calendarService.save(new Calendar(calendarData.getCode(), calendarData.getName()))
                        .getUuid();
            } else {
                if (!calendarService.compareName(calendarUuid, calendarData.getName())) {
                    calendarService.setName(calendarUuid, calendarData.getName());
                }
                if (calendarData.getToGregorianShift() != null
                        && !calendarService.compareToGregorianShift(calendarUuid, calendarData.getToGregorianShift())) {
                    calendarService.setToGregorianShift(calendarUuid, calendarData.getToGregorianShift());
                }
            }
        });
        logger.info("Initialized " + calendars.size() + " calendars.");
    }

    // Get all calendar data
    private List<CalendarData> getAllCalendarData() {
        List<CalendarData> calendars = new ArrayList<>();

        // Unknown
        calendars.add(new CalendarData("inconnu", "Unknown", null));

        // Ancient Greek and Hellenistic (обычно используют эры с разными точками
        // отсчета)
        calendars.add(new CalendarData("alabanda", "Alabanda era", null)); // неизвестный сдвиг
        calendars.add(new CalendarData("alexandria-troas", "Alexandria Troas era", null));
        calendars.add(new CalendarData("anazarbus", "Anazarbus era", null));
        calendars.add(new CalendarData("aegospotami", "Ancient Greece - Aegospotami era", null));
        calendars.add(new CalendarData("aspendos", "Aspendos era", null));
        calendars.add(new CalendarData("bosporean", "Bosporan era", null));
        calendars.add(new CalendarData("bosporan-kingdom-asander", "Bosporan Kingdom - Asander era", null));
        calendars.add(new CalendarData("demetrias", "Demetrias era", null));
        calendars.add(new CalendarData("ephesus-attalus-ii", "Ephesus - Attalus II era", null));
        calendars.add(new CalendarData("ephesus-attalus-iii", "Ephesus - Attalus III era", null));
        calendars.add(new CalendarData("ephesus", "Ephesus era", null));
        calendars.add(new CalendarData("eusebia", "Eusebia era", null));
        calendars.add(new CalendarData("gaba", "Gaba era", null));
        calendars.add(new CalendarData("galatia-amyntas", "Galatia - Amyntas era", null));
        calendars.add(new CalendarData("isinda", "Isinda era", null));
        calendars.add(new CalendarData("korone", "Korone era", null));
        calendars.add(new CalendarData("laodicea", "Laodicea era", null));
        calendars.add(new CalendarData("magydus", "Magydus era", null));
        calendars.add(new CalendarData("marisa", "Marisa era", null));
        calendars.add(new CalendarData("nicaea-lysimachus", "Nicaea - Lysimachus era", null));
        calendars.add(new CalendarData("oenoanda", "Oenoanda era", null));
        calendars.add(new CalendarData("perga", "Perga era", null));
        calendars.add(new CalendarData("pergamon", "Pergamon era", null));
        calendars.add(new CalendarData("phaselis", "Phaselis era", null));
        calendars.add(new CalendarData("pompeian", "Pompeian era", null));
        calendars.add(new CalendarData("pompeian-gaza", "Pompeian era of Gaza", null));
        calendars.add(new CalendarData("samos", "Samos era", null));
        calendars.add(new CalendarData("sillyon", "Sillyon era", null));
        calendars.add(new CalendarData("sinope", "Sinope era", null));
        calendars.add(new CalendarData("soter", "Soter era", null));
        calendars.add(new CalendarData("termessos", "Termessos era", null));
        calendars.add(new CalendarData("thessalonica", "Thessalonica era", null));
        calendars.add(new CalendarData("zankle", "Zankle era", null));

        // Armenian
        calendars.add(new CalendarData("armenia-artavasdes-ii", "Armenia - Artavasdes II era", null));
        calendars.add(new CalendarData("armenian", "Armenian era", 551)); // Армянский календарь начинается в 551 году
                                                                          // н.э.

        // Arsakid/Parthian
        calendars.add(new CalendarData("arsakid", "Arsakid / Parthian era", null));

        // Phoenician cities
        calendars.add(new CalendarData("aradus-gerashtart", "Aradus - Gerashtart era", null));
        calendars.add(new CalendarData("ashkelon", "Ashkelon era", null));
        calendars.add(new CalendarData("kition-pumiathon", "Kition - Pumiathon era", null));
        calendars.add(new CalendarData("phoenician", "Phoenician era", null));
        calendars.add(new CalendarData("sidon-abdashtart-i", "Sidon - Abdashtart I era", null));
        calendars.add(new CalendarData("sidon-abdashtart-ii", "Sidon - Abdashtart II era", null));
        calendars.add(new CalendarData("sidon-baal-sillem-ii", "Sidon - Ba'al Sillem II era", null));
        calendars.add(new CalendarData("sidon-evagoras-ii", "Sidon - Evagoras II era", null));
        calendars.add(new CalendarData("sidon-mazaeus", "Sidon - Mazaeus era", null));
        calendars.add(new CalendarData("sidon-tennes", "Sidon - Tennes era", null));
        calendars.add(new CalendarData("sidon-post-alexander", "Sidon post-Alexander era", null));
        calendars.add(new CalendarData("sidon-pre-alexander", "Sidon pre-Alexander era", null));
        calendars.add(new CalendarData("tyre-abdashtart-of-tyre", "Tyre - Abdashtart of Tyre era", null));
        calendars.add(new CalendarData("tyre-azemilcus", "Tyre - Azemilcus era", null));
        calendars.add(new CalendarData("tyre", "Tyre era", null));

        // Buddhist - начинается в 543 году до н.э.
        calendars.add(new CalendarData("chulasakarat", "Buddhist (Chulasakarat)", -543));

        // Byzantine
        calendars.add(new CalendarData("byzance-heraclius", "Byzance - Heraclius era", null));
        calendars.add(new CalendarData("byzantin", "Byzantine", null));

        // Caesarean
        calendars.add(new CalendarData("caesarean", "Caesarean era", null));
        calendars.add(new CalendarData("caesarean-aigeae", "Caesarean era of Aigeae", null));
        calendars.add(new CalendarData("caesarean-laodicea", "Caesarean era of Laodicea", null));

        // Cappadocia
        calendars.add(new CalendarData("cappadocia-archelaus", "Cappadocia - Archelaus era", null));
        calendars.add(new CalendarData("cappadocia-ariarathes-ix", "Cappadocia - Ariarathes IX era", null));
        calendars.add(new CalendarData("cappadocia-ariarathes-v", "Cappadocia - Ariarathes V era", null));
        calendars.add(new CalendarData("cappadocia-ariarathes-vi", "Cappadocia - Ariarathes VI era", null));
        calendars.add(new CalendarData("cappadocia-ariarathes-vii", "Cappadocia - Ariarathes VII era", null));
        calendars.add(new CalendarData("cappadocia-ariarathes-viii", "Cappadocia - Ariarathes VIII era", null));
        calendars.add(new CalendarData("cappadocia-ariarathes-x", "Cappadocia - Ariarathes X era", null));
        calendars.add(new CalendarData("cappadocia-ariobarzanes-i", "Cappadocia - Ariobarzanes I era", null));
        calendars.add(new CalendarData("cappadocia-ariobarzanes-ii", "Cappadocia - Ariobarzanes II era", null));
        calendars.add(new CalendarData("cappadocia-ariobarzanes-iii", "Cappadocia - Ariobarzanes III era", null));

        // Chinese
        calendars.add(new CalendarData("chinese-baoqing-era", "Chinese - Baoqing era", null));
        calendars.add(new CalendarData("chinese-baoyou-era", "Chinese - Baoyou era", null));
        calendars.add(new CalendarData("chinese-chunxi-era", "Chinese - Chunxi era", null));
        calendars.add(new CalendarData("chinese-chunyou-era", "Chinese - Chunyou era", null));
        calendars.add(new CalendarData("chinese-dade-era", "Chinese - Dade era", null));
        calendars.add(new CalendarData("chinese-daoguang-era", "Chinese - Daoguang era", null));
        calendars.add(new CalendarData("chinese-datong-era", "Chinese - Datong era", null));
        calendars.add(new CalendarData("chinese-duanping-era", "Chinese - Duanping era", null));
        calendars.add(new CalendarData("china-kuang-hsu", "Chinese - Guangxu era", null));
        calendars.add(new CalendarData("chinese-hongxian-era", "Chinese - Hongxian era", null));
        calendars.add(new CalendarData("chinese-jiading-era", "Chinese - Jiading era", null));
        calendars.add(new CalendarData("chinese-jiaqing-era", "Chinese - Jiaqing era", null));
        calendars.add(new CalendarData("chinese-jiatai-era", "Chinese - Jiatai era", null));
        calendars.add(new CalendarData("chinese-jiaxi-era", "Chinese - Jiaxi era", null));
        calendars.add(new CalendarData("chinese-jingding-era", "Chinese - Jingding era", null));
        calendars.add(new CalendarData("chinese-kaiqing-era", "Chinese - Kaiqing era", null));
        calendars.add(new CalendarData("chinese-kaixi-era", "Chinese - Kaixi era", null));
        calendars.add(new CalendarData("chinese-kangde-era", "Chinese - Kangde era", null));
        calendars.add(new CalendarData("chinese-qianlong-era", "Chinese - Qianlong era", null));
        calendars.add(new CalendarData("chinese-qingyuan-era", "Chinese - Qingyuan era", null));
        calendars.add(new CalendarData("chinese-shaoding-era", "Chinese - Shaoding era", null));
        calendars.add(new CalendarData("chinese-shaoxi-era", "Chinese - Shaoxi era", null));
        calendars.add(new CalendarData("chinese-taiding-era", "Chinese - Taiding era", null));
        calendars.add(new CalendarData("chinese-xianchun-era", "Chinese - Xianchun era", null));
        calendars.add(new CalendarData("chinese-xianfeng-era", "Chinese - Xianfeng era", null));
        calendars.add(new CalendarData("china-puyi-emperor", "Chinese - Xuantong era", null));
        calendars.add(new CalendarData("chinese-zhiyuan2-era", "Chinese - Zhiyuan era (Kublai Khan)", null));
        calendars.add(new CalendarData("chinese-zhiyuan-era", "Chinese - Zhiyuan era (Toghon Temür Khan)", null));
        calendars.add(new CalendarData("chinese-zhizhi-era", "Chinese - Zhizhi era", null));
        calendars.add(new CalendarData("chinese-cyclcal-1144", "Chinese cyclical (cycle starting in 1144)", null));
        calendars.add(new CalendarData("chinese-cyclcal-1324", "Chinese cyclical (cycle starting in 1324)", null));
        calendars.add(new CalendarData("chinese-cyclcal-1624", "Chinese cyclical (cycle starting in 1624)", null));
        calendars.add(new CalendarData("chinese-cyclcal-1684", "Chinese cyclical (cycle starting in 1684)", null));
        calendars.add(new CalendarData("chinese-cyclcal-1864", "Chinese cyclical (cycle starting in 1864)", null));
        calendars.add(new CalendarData("chinese-cyclcal-1924", "Chinese cyclical (cycle starting in 1924)", null));
        calendars.add(new CalendarData("taiwanese", "Chinese republican", null));
        calendars.add(new CalendarData("chinese-soviet", "Chinese soviet republican", null));

        // Spanish era
        calendars.add(new CalendarData("es-safar", "Es-Safar / Spanish era", null));
        calendars.add(new CalendarData("spanish_era", "Spanish era", null));

        // Ethiopian
        calendars.add(new CalendarData("ethiopien", "Ethiopian", null));

        // French republican
        calendars.add(new CalendarData("republicain", "French republican", null));

        // Gregorian/Julian (main calendar) - без сдвига, это базовый календарь
        calendars.add(new CalendarData("gregorien", "Gregorian/Julian", 0));

        // Haitian republican
        calendars.add(new CalendarData("haitian-republican", "Haitian republican", null));

        // Hebrew - начинается в 3761 году до н.э.
        calendars.add(new CalendarData("hebreu", "Hebrew", -3761));

        // Indian
        calendars.add(new CalendarData("fasli-era", "India - Fasli era", null));
        calendars.add(new CalendarData("malabar-era", "India - Kollam era", 825)); // 825 год н.э.
        calendars.add(new CalendarData("indian-mauludi-era", "India - Mauludi era", null));
        calendars.add(new CalendarData("tripura-era", "India - Tripura era", null));
        calendars.add(new CalendarData("nepalais", "Vikram Samvat", -57)); // 57 год до н.э.
        calendars.add(new CalendarData("saka", "Shaka era", 78)); // 78 год н.э.
        calendars.add(new CalendarData("nepal_sambat", "Nepal Sambat", 879)); // 879 год н.э.
        calendars.add(new CalendarData("saka-era-nepal", "Nepal - Saka era", 78)); // 78 год н.э.

        // Iranian/Persian - начинается в 622 году н.э. (совпадает с исламским)
        calendars.add(new CalendarData("persan", "Iranian - Persian", 622));
        calendars.add(new CalendarData("iran_imperial", "Iranian imperial", null));

        // Islamic - начинается в 622 году н.э.
        calendars.add(new CalendarData("islamic-posthumous", "Islamic - Muhammad posthumous era", null));
        calendars.add(new CalendarData("musulman", "Islamic (Hijri)", 622));

        // Japanese - различные эры
        calendars.add(new CalendarData("japonais_heisei", "Japanese - Heisei era", 1989)); // 1989-2019
        calendars.add(new CalendarData("japonais_koki", "Japanese - Kōki", -660)); // 660 год до н.э.
        calendars.add(new CalendarData("japonais_meiji", "Japanese - Meiji era", 1868)); // 1868-1912
        calendars.add(new CalendarData("japonais_reiwa", "Japanese - Reiwa era", 2019)); // 2019-
        calendars.add(new CalendarData("japonais_showa", "Japanese - Shōwa era", 1926)); // 1926-1989
        calendars.add(new CalendarData("japonais_taisho", "Japanese - Taishō era", 1912)); // 1912-1926

        // Juche (North Korean) - начинается в 1912 году
        calendars.add(new CalendarData("juche", "Juche", 1912));

        // Judean
        calendars.add(new CalendarData("judea-alexander-jannaeus", "Judea - Alexander Jannaeus era", null));
        calendars.add(new CalendarData("judea-herod-antipas", "Judea - Herod Antipas era", null));
        calendars.add(new CalendarData("judea-herod-i", "Judea - Herod I era", null));

        // Korean
        calendars.add(new CalendarData("coreen_dangun", "Korean - Dangun", -2333)); // 2333 год до н.э.
        calendars.add(new CalendarData("coreen_gwangmu", "Korean - Gwangmu era", null));
        calendars.add(new CalendarData("coreen_joseon", "Korean - Joseon era", 1392)); // 1392-1910
        calendars.add(new CalendarData("sunjong-era", "Korean - Sunjong era", null));

        // Mauretania
        calendars.add(new CalendarData("mauretania-juba-ii-era", "Mauretania - Juba II era", null));

        // Mongolian
        calendars.add(new CalendarData("mongolian", "Mongolian", null));

        // Nabataean
        calendars.add(new CalendarData("nabataea-aretas-iv", "Nabataea - Aretas IV era", null));
        calendars.add(new CalendarData("nabataea-malchios-ii", "Nabataea - Malchios II era", null));
        calendars.add(new CalendarData("nabataea-malichos", "Nabataea - Malichos era", null));
        calendars.add(new CalendarData("nabataea-obodas-ii", "Nabataea - Obodas II era", null));
        calendars.add(new CalendarData("nabataea-obodas-iii", "Nabataea - Obodas III era", null));
        calendars.add(new CalendarData("nabataea-rabbel-ii", "Nabataea - Rabbel II era", null));

        // Ptolemaic Kingdom
        calendars.add(
                new CalendarData("ptolemaic-kingdom-cleopatra-iii", "Ptolemaic Kingdom - Cleopatra III era", null));
        calendars.add(new CalendarData("ptolemaic-kingdom-cleopatra-vii-in-egypt",
                "Ptolemaic Kingdom - Cleopatra VII in Egypt era", null));
        calendars.add(new CalendarData("ptolemaic-kingdom-cleopatra-vii-in-phoenicia",
                "Ptolemaic Kingdom - Cleopatra VII in Phoenicia era", null));
        calendars.add(new CalendarData("ptolemaic-kingdom-ptolemy-ii", "Ptolemaic Kingdom - Ptolemy II era", null));
        calendars.add(new CalendarData("ptolemaic-kingdom-ptolemy-iii", "Ptolemaic Kingdom - Ptolemy III era", null));
        calendars.add(new CalendarData("ptolemaic-kingdom-ptolemy-v", "Ptolemaic Kingdom - Ptolemy V era", null));
        calendars.add(new CalendarData("ptolemaic-kingdom-ptolemy-vi", "Ptolemaic Kingdom - Ptolemy VI era", null));
        calendars.add(new CalendarData("ptolemaic-kingdom-ptolemy-viii", "Ptolemaic Kingdom - Ptolemy VIII era", null));
        calendars.add(new CalendarData("ptolemaic-kingdom-ptolemy-x", "Ptolemaic Kingdom - Ptolemy X era", null));
        calendars.add(new CalendarData("ptolemaic-kingdom-ptolemy-xii", "Ptolemaic Kingdom - Ptolemy XII era", null));
        calendars.add(new CalendarData("ptolemaic-kingdom-ptolemy-xv", "Ptolemaic Kingdom - Ptolemy XV era", null));

        // Roman
        calendars.add(new CalendarData("actian", "Rome - Actian era", null));
        calendars.add(new CalendarData("rome-augustus", "Rome - Augustus era", null));
        calendars.add(new CalendarData("rome-diocletian", "Rome - Diocletian era", null));
        calendars.add(new CalendarData("rome-domitian", "Rome - Domitian era", null));
        calendars.add(new CalendarData("rome-galba", "Rome - Galba era", null));
        calendars.add(new CalendarData("rome-gallienus ", "Rome - Gallienus era", null));
        calendars.add(new CalendarData("rome-gordian-iii", "Rome - Gordian III era", null));
        calendars.add(new CalendarData("rome-hadrian", "Rome - Hadrian era", null));
        calendars.add(new CalendarData("rome-julius-caesar", "Rome - Julius Caesar era", null));
        calendars.add(new CalendarData("rome-mark-anthony", "Rome - Mark Anthony era", null));
        calendars.add(new CalendarData("rome-nerva", "Rome - Nerva era", null));
        calendars.add(new CalendarData("rome-trajan", "Rome - Trajan era", null));

        // Rumi
        calendars.add(new CalendarData("rumi", "Rumi", null));

        // Sassanid Empire
        calendars.add(new CalendarData("sassanid_ardzshir_iii", "Sassanid Empire - Ardashir III reign", null));
        calendars.add(new CalendarData("sassanid_azarmidukht", "Sassanid Empire - Azarmidukht reign", null));
        calendars.add(new CalendarData("sassanid_buran", "Sassanid Empire - Buran reign", null));
        calendars.add(new CalendarData("sassanid_hormazd_iv", "Sassanid Empire - Hormazd IV reign", null));
        calendars.add(new CalendarData("sassanid_kawad_i", "Sassanid Empire - Kawad I reign", null));
        calendars.add(new CalendarData("sassanid_kawad_ii", "Sassanid Empire - Kawad II reign", null));
        calendars.add(new CalendarData("sassanid_khusro_i", "Sassanid Empire - Khusro I reign", null));
        calendars.add(new CalendarData("sassanid_khusro_ii", "Sassanid Empire - Khusro II reign", null));
        calendars.add(new CalendarData("sassanid_yadzgerd_iii", "Sassanid Empire - Yazdgerd III reign", null));

        // Seleucid
        calendars.add(new CalendarData("seleucia", "Seleucia era", null));
        calendars.add(new CalendarData("seleucid", "Seleucid era", null));
        calendars.add(new CalendarData("seleucid-kingdom-tryphon", "Seleucid Kingdom - Tryphon era", null));

        // Thai
        calendars.add(new CalendarData("thailandais_solaire", "Thai", null));
        calendars.add(new CalendarData("rama-samvat", "Thailand - Rama era", null));

        // Tibetan
        calendars.add(new CalendarData("tibetain-13", "Tibetan (13th cycle)", null));
        calendars.add(new CalendarData("tibetain-15", "Tibetan (15th cycle)", null));
        calendars.add(new CalendarData("tibetain", "Tibetan (16th cycle)", null));
        calendars.add(new CalendarData("tibetain-1", "Tibetan (1st cycle)", null));

        // Tripoli
        calendars.add(new CalendarData("tripoli era", "Tripoli era", null));

        // Vietnamese
        calendars.add(new CalendarData("vietnamese-minh-m-7841-ng", "Vietnam - Minh Mạng era", null));
        calendars.add(new CalendarData("vietnamese-tran", "Vietnam - Trần dynasty", null));

        // Viminacium
        calendars.add(new CalendarData("viminacium", "Viminacium era", null));

        return calendars;
    }

}
