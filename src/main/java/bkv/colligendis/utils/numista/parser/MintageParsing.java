package bkv.colligendis.utils.numista.parser;

import java.util.UUID;

import bkv.colligendis.database.entity.numista.Variant;
import bkv.colligendis.database.entity.numista.CatalogueReference;
import bkv.colligendis.database.entity.numista.Mark;
import bkv.colligendis.database.entity.numista.Calendar;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import bkv.colligendis.utils.numista.NumistaPartParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MintageParsing extends PartParser {
    private static final Logger logger = LogManager.getLogger(MintageParsing.class);

    public MintageParsing() {
        super((pageParser) -> {

            ParsingResult result = ParsingResult.NOT_CHANGED;

            Element anneesElement = pageParser.getNumistaPage().selectFirst("#annees");
            if (anneesElement == null) {
                logger.error("Can't find 'annees' while parsing page with nid: {}", pageParser.getNid());
                return ParsingResult.ERROR;
            }

            HashMap<String, String> calendrier = getAttributeWithTextSelectedOption(anneesElement, "#calendrier");
            if (calendrier == null) {
                logger.error("Can't find Calendar while parsing page with nid: {}", pageParser.getNid());
                return ParsingResult.ERROR;
            }

            UUID calendarUuid = calendarService.findUuidByCode(calendrier.get("value"));
            String calendarCode = calendrier.get("value");

            if (calendarUuid == null) {
                logger.error("Can't find Calendar with code: {} while parsing page with nid: {}", calendarCode,
                        pageParser.getNid());
                return ParsingResult.ERROR;
            }

            Elements trElements = anneesElement.select("tr");

            for (Element trElement : trElements) {
                Elements tdElements = trElement.select("td");

                // Variant variant = null;
                UUID variantUuid = null;

                String variantNid = null;
                if (!tdElements.isEmpty() && !tdElements.get(0).children().isEmpty()) {
                    variantNid = tdElements.get(0).children().get(0).attr("name");
                    if (variantNid.startsWith("nd")) {
                        variantNid = variantNid.substring(2);
                        variantUuid = variantService.findUuidByNid(variantNid);
                    } else if (variantNid.startsWith("nouveau")) {
                        continue;
                    } else {
                        logger.warn("Find string with not 'nd' and 'nouveau' prefix while parsing page with nid: {}",
                                pageParser.getNid());
                        continue;
                    }
                } else {
                    continue;
                }
                if (variantUuid == null) {
                    variantUuid = variantService.save(new Variant(variantNid)).getUuid();
                    nTypeService.addVariant(pageParser.getNTypeUuid(), variantUuid);
                    logger.debug("Add new variants for NType with nid: {}", pageParser.getNid());
                }

                // Fill variant
                for (Element tdElement : tdElements) {
                    switch (tdElement.attr("class")) {
                        case "date_check" -> {

                            Element dateCheckElement = tdElement.selectFirst("input");
                            if (dateCheckElement == null || !dateCheckElement.attr("checked").equals("checked")) {
                                logger.debug("The date_check is not checked while parsing page with nid: {}",
                                        pageParser.getNid());
                                continue;
                            }

                            String name = dateCheckElement.attr("name").substring(2);

                            Element datesElement = pageParser.getNumistaPage().selectFirst("#dates" + name);
                            if (datesElement == null) {
                                logger.error(
                                        "The date_check is checked but can't find Element with id=#dates{} while parsing page with nid: {}",
                                        name, pageParser.getNid());
                                continue;
                            }
                            Elements inputDatesElements = datesElement.select("input");
                            if (inputDatesElements.size() != 2) {
                                logger.error(
                                        "The date_check is checked but can't find 2 input elements while parsing page with nid: {}",
                                        pageParser.getNid());
                                continue;
                            }
                            String dateFrom = inputDatesElements.get(0).attr("value");
                            String dateTill = inputDatesElements.get(1).attr("value");

                            if (StringUtils.isNumeric(dateFrom)) {
                                UUID yearFromUuid = yearService.findYearUuidByValueAndCalendarCode(
                                        Integer.valueOf(dateFrom),
                                        Calendar.GREGORIAN_CODE);
                                if (!variantService.compareYearFrom(variantUuid, yearFromUuid)) {
                                    variantService.setYearFrom(variantUuid, yearFromUuid);
                                }
                            }
                            if (StringUtils.isNumeric(dateTill)) {
                                UUID yearTillUuid = yearService.findYearUuidByValueAndCalendarCode(
                                        Integer.valueOf(dateTill),
                                        Calendar.GREGORIAN_CODE);
                                if (!variantService.compareYearTill(variantUuid, yearTillUuid)) {
                                    variantService.setYearTill(variantUuid, yearTillUuid);
                                }
                            }

                        }
                        case "date_year" -> {
                            Element millesimeElement = tdElement.selectFirst("input");
                            if (millesimeElement == null) {
                                logger.error("Can't find 'millesime' input while parsing page with nid: {}",
                                        pageParser.getNid());
                                continue;
                            }
                            if (millesimeElement.attr("value").isEmpty()) {
                                logger.error("Value of 'millesime' is empty input while parsing page with nid: {}",
                                        pageParser.getNid());
                                continue;
                            }

                            if (millesimeElement.attr("name").startsWith("millesime")) {
                                Integer yearValue = Integer.valueOf(millesimeElement.attr("value"));
                                UUID yearUuid = yearService.findYearUuidByValueAndCalendarCode(yearValue, calendarCode);
                                if (yearUuid == null) {
                                    logger.error(
                                            "Can't find Year with value: {} and calendar code: {} while parsing page with nid: {}",
                                            yearValue, calendarCode, pageParser.getNid());
                                    continue;
                                }
                                if (!variantService.compareYear(variantUuid, yearUuid)) {
                                    variantService.setYear(variantUuid, yearUuid);
                                }
                            }
                        }
                        case "date_mint" -> {
                            Element atelierElement = tdElement.selectFirst("input");
                            if (atelierElement == null) {
                                logger.error("Can't find 'atelier' input while parsing page with nid: {}",
                                        pageParser.getNid());
                                continue;
                            }
                            if (atelierElement.attr("name").startsWith("atelier")
                                    && !atelierElement.attr("value").isEmpty()) {

                                if (!variantService.compareMintLetter(variantUuid, atelierElement.attr("value"))) {
                                    variantService.setMintLetter(variantUuid, atelierElement.attr("value"));
                                }
                            }
                        }
                        case "date_mark" -> {

                            Elements selectOptions = tdElement.select("option");
                            ArrayList<String> marks = new ArrayList<>();
                            for (Element optionElement : selectOptions) {
                                marks.add(optionElement.attr("value"));
                            }
                            List<UUID> foundMarks = marks.stream().map(nid -> {
                                UUID markUuid = markService.findUuidByNid(nid);
                                if (markUuid == null) {
                                    markUuid = markService.save(new Mark(nid)).getUuid();
                                }
                                assert markUuid != null;
                                return markUuid;
                            }).collect(Collectors.toList());

                            if (variantService.equateMarks(variantUuid, foundMarks)) {
                                result = ParsingResult.CHANGED;
                            }

                        }
                        case "date_mintage" -> {
                            Element input = tdElement.selectFirst("input");
                            if (input == null) {
                                logger.debug("Can't find 'mintage' input while parsing page with nid: {}",
                                        pageParser.getNid());
                                continue;
                            }
                            String mintage_str = NumistaPartParser.getAttribute(input, "value");

                            if (mintage_str == null) {
                                logger.debug("Value of 'mintage' is empty input while parsing page with nid: {}",
                                        pageParser.getNid());
                                continue;
                            }
                            int mintage = Integer.parseInt(mintage_str.replaceAll(" ", ""));
                            if (!variantService.compareMintage(variantUuid, mintage)) {
                                variantService.setMintage(variantUuid, mintage);
                            }
                        }
                        case "" -> {
                            /*
                             * <div class="reference">
                             * <select style="width: 6em;" name="first_ref290" class="catalogue_select"
                             * data-edit="1">
                             * <option value="109">Gad 1789</option>
                             * </select>
                             * <span style="width: 1em">#</span>
                             * <input class="first_version_reference" type="text" name="first_number290"
                             * value="775">
                             * <span style="width: 1em;"></span>
                             * </div>
                             * 
                             * <div id="second_reference290" class="reference" style="display:flex;">
                             * <select style="width: 6em;" name="second_ref290" class="catalogue_select"
                             * data-edit="1">
                             * <option value="34">Franc 2014</option>
                             * </select>
                             * <span style="width: 1em">#</span>
                             * <input class="second_version_reference" type="text" name="second_number290"
                             * value="344/2">
                             * <span style="width: 1em"></span>
                             * </div>
                             */
                            List<ReferenceToCatalogue> references = new ArrayList<>();

                            Elements divs = tdElement.select("div");
                            for (Element divElement : divs) {
                                Element optionElement = divElement.selectFirst("option");
                                Element inputElement = divElement.selectFirst("input");

                                if (optionElement == null || inputElement == null) {
                                    logger.debug(
                                            "Can't find CatalogueReference 'option' or 'input' while parsing page with nid: {}",
                                            pageParser.getNid());
                                    continue;
                                }

                                references.add(new ReferenceToCatalogue(
                                        optionElement.attr("value"),
                                        optionElement.text(),
                                        inputElement.attr("value")));
                            }
                            List<UUID> foundReferences = references.stream().map(reference -> {
                                UUID referenceUuid = catalogueReferenceService.findUuidByNumberAndCatalogueNid(
                                        reference.getNumber(), reference.getCatalogueNid());
                                if (referenceUuid != null) {
                                    return referenceUuid;
                                }

                                UUID catalogueUuid = catalogueService.findUuidByNid(reference.getCatalogueNid());
                                if (catalogueUuid == null) {
                                    ReferenceNumberParsing
                                            .parseReferenceCataloguesByCode(reference.getCatalogueCode());
                                    catalogueUuid = catalogueService.findUuidByNid(reference.getCatalogueNid());
                                }
                                assert catalogueUuid != null;

                                referenceUuid = catalogueReferenceService.save(new CatalogueReference()).getUuid();
                                catalogueReferenceService.setNumber(referenceUuid, reference.getNumber());
                                catalogueReferenceService.setCatalogue(referenceUuid, catalogueUuid);
                                assert referenceUuid != null;
                                return referenceUuid;
                            }).collect(Collectors.toList());

                            if (variantService.equateCatalogueReferences(variantUuid, foundReferences)) {
                                result = ParsingResult.CHANGED;
                            }
                        }

                        case "yearline_comment" -> {

                            String comment = NumistaPartParser.getAttribute(tdElement.selectFirst("input"), "value");
                            if (comment != null) {
                                if (!variantService.compareComment(variantUuid, comment)) {
                                    variantService.setComment(variantUuid, comment);
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
