package bkv.colligendis.utils.numista.parser;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import org.jsoup.nodes.Element;

import bkv.colligendis.database.entity.numista.Series;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SeriesParsing extends PartParser {
    private static final Logger logger = LogManager.getLogger(SeriesParsing.class);

    public SeriesParsing() {
        super((pageParser) -> {
            ParsingResult result = ParsingResult.NOT_CHANGED;

            Element seriesElement = pageParser.getNumistaPage().selectFirst("#series");
            if (seriesElement == null)
                return ParsingResult.NOT_CHANGED;

            Element seriesOption = seriesElement.selectFirst("option");
            if (seriesOption == null)
                return ParsingResult.NOT_CHANGED;

            HashMap<String, String> map = getAttributeWithTextSingleOption(seriesOption, "value");

            if (map == null || map.get("value") == null || map.get("value").isEmpty() || map.get("text") == null
                    || map.get("text").isEmpty())
                return ParsingResult.NOT_CHANGED;

            String series = Objects.requireNonNull(map).get("text");
            String seriesNid = Objects.requireNonNull(map).get("value");

            UUID foundSeriesUuid = seriesService.findUuidByName(series);
            if (foundSeriesUuid == null) {
                foundSeriesUuid = seriesService.save(new Series(seriesNid, series)).getUuid();
            }

            if (!nTypeService.hasRelationshipToSeries(pageParser.getNTypeUuid(), foundSeriesUuid)) {
                nTypeService.setSeries(pageParser.getNTypeUuid(), foundSeriesUuid);
                result = ParsingResult.CHANGED;
            }

            return result;
        });

        this.partName = "Series";
    }

    /*
     * If the commemorative coin is part of a series, specify the name of the
     * series. Ensure that the name of the series is consistent for all the coins.
     * For commemorative series, do not repeat the word “series”.
     * Swiss mountains
     * Swiss mountain series
     * Circulating non-commemorative coins are usually issued in suites of
     * denominations covering a spectrum of values. For example, from 1 cent up to 2
     * euros. You may assign the same series name to all the denominations of a
     * particular suite. If possible, name the suite using a distinguishing design
     * or physical feature. Otherwise, name the series after the year when the first
     * coin of the suite was issued. Series should not be named using ordinal
     * numerals. For circulating coins, include the word “series” in the name.
     * 16 orbits series
     * Copper-nickel series
     * 1991 series
     * 3rd series || 3rd issue || 3rd type
     * Use sentence case.
     * Copper-nickel series
     * Copper-Nickel Series || copper-nickel series
     * For a given issuer, series names should be unique: two different series of
     * the same issuer should not have the same name.
     */

}
