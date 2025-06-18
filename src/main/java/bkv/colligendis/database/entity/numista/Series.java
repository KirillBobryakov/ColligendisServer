package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

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

  Information takes from https://en.numista.com/help/series-110.html
 */
@Node("SERIES")
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Series extends AbstractEntity {

    private String name;

}
