package bkv.colligendis.utils.numista;

import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SeriesParser extends NumistaPartParser {


    /*
    If the commemorative coin is part of a series, specify the name of the series. Ensure that the name of the series is consistent for all the coins. For commemorative series, do not repeat the word “series”.
        Swiss mountains
        Swiss mountain series
        Circulating non-commemorative coins are usually issued in suites of denominations covering a spectrum of values. For example, from 1 cent up to 2 euros. You may assign the same series name to all the denominations of a particular suite. If possible, name the suite using a distinguishing design or physical feature. Otherwise, name the series after the year when the first coin of the suite was issued. Series should not be named using ordinal numerals. For circulating coins, include the word “series” in the name.
        16 orbits series
        Copper-nickel series
        1991 series
        3rd series  ||  3rd issue  ||  3rd type
        Use sentence case.
        Copper-nickel series
        Copper-Nickel Series  ||  copper-nickel series
        For a given issuer, series names should be unique: two different series of the same issuer should not have the same name.
     */

    public SeriesParser() {
        super((page, nType) -> {

            HashMap<String, String> map = getAttributeWithTextSelectedOption(page, "#series");

            if(map == null) return ParseEvent.NOT_CHANGED;

            String series = Objects.requireNonNull(map).get("text");
            DebugUtil.printProperty("series", series, false, false, false);


            if (series != null && !series.isEmpty()) {

                if (nType.getSeries() != null) {
                    if (nType.getSeries().getName().equals(series)) {
                        DebugUtil.showInfo(SeriesParser.class, "The Series of existing NType is equal with Series on the page.");
                        return ParseEvent.NOT_CHANGED;
                    } else {
                        nType.setSeries(N4JUtil.getInstance().numistaService.seriesService.findByNameOrCreate(series));
                        DebugUtil.showWarning(CommemoratedEventParser.class, "The Series of existing NType is not equal with Series on the page.");
                        return ParseEvent.CHANGED;
                    }
                }

                nType.setSeries(N4JUtil.getInstance().numistaService.seriesService.findByNameOrCreate(series));
                return ParseEvent.CHANGED;

            }

            return ParseEvent.NOT_CHANGED;
        });

        this.partName = "Series";
    }
}
