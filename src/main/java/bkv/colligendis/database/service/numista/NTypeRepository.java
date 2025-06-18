package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface NTypeRepository2 extends Neo4jRepository<NType, UUID> {

    NType findByNid(String nid);

//    @Query("MATCH (n:NTYPE) WHERE n.nid='201394' " +
//            "OPTIONAL MATCH (n)-[uc:UNDER_CATEGORY]->(c:CATEGORY) OPTIONAL MATCH (n)-[ib:ISSUED_BY]->(i:ISSUER) OPTIONAL MATCH (n)-[dor:DURING_OF_RULER]->(r:ISSUER) OPTIONAL MATCH (n)-[pb:PROVIDED_BY]->(ie:ISSUING_ENTITY) OPTIONAL MATCH (n)-[ucur:UNDER_CURRENCY]->(cur:CURRENCY) OPTIONAL MATCH (n)-[ht:HAS_TYPE]->(t:TYPE) OPTIONAL MATCH (n)-[cf:COMMEMORATE_FOR]->(ce:COMMEMORATED_EVENT) OPTIONAL MATCH (n)-[hs:HAS_SERIES]->(s:SERIES) OPTIONAL MATCH (n)-[hr:HAS_REFERENCE]->(cr:CATALOGUE_REFERENCE) OPTIONAL MATCH (n)-[hc:HAS_COMPOSITION]->(comp:COMPOSITION) OPTIONAL MATCH (n)-[hsh:HAS_SHAPE]->(sh:SHAPE) OPTIONAL MATCH (n)-[wt:WITH_TECHNIQUE]->(tq:TECHNIQUE) OPTIONAL MATCH (n)-[hob:HAS_OBVERSE]->(ntpo:NTYPE_PART) OPTIONAL MATCH (n)-[hrv:HAS_REVERSE]->(ntpr:NTYPE_PART) OPTIONAL MATCH (n)-[hed:HAS_EDGE]->(ntpe:NTYPE_PART) OPTIONAL MATCH (n)-[hsm:HAS_SPECIFIED_MINT]->(sm:SPECIFIED_MINT) OPTIONAL MATCH (n)-[hw:HAS_WATERMARK]->(ntpw:NTYPE_PART) OPTIONAL MATCH (n)-[prb:PRINTED_BY]->(p:PRINTER) OPTIONAL MATCH (n)-[rw:REFERENCED_WITH]->(ntag:NTAG) OPTIONAL MATCH (n)-[dw:DATED_WITHIN]->(cal:CALENDAR) " +
////            "RETURN count(n)" +
//            "RETURN DISTINCT n, collect(uc), collect(c), collect(ib), collect(i), collect(dor), collect(r), collect(pb), collect(ie), collect(ucur), collect(cur), collect(ht), collect(t), collect(cf), collect(ce), collect(hs), collect(s), collect(hr), collect(cr), collect(hc), collect(comp), collect(hsh), collect(sh), collect(wt), collect(tq), collect(hob), collect(ntpo), collect(hrv), collect(ntpr), collect(hed), collect(ntpe), collect(hsm), collect(sm), collect(hw), collect(ntpw), collect(prb), collect(p), collect(rw), collect(ntag), collect(dw), collect(cal)" +
//            "")
//    NType findByNidAll(String nid);
//
//    @Query("MATCH (n:NTYPE) WHERE n.nid='201394' " +
//            "OPTIONAL MATCH (n)-[uc:UNDER_CATEGORY]->(c:CATEGORY) OPTIONAL MATCH (n)-[ib:ISSUED_BY]->(i:ISSUER) OPTIONAL MATCH (n)-[dor:DURING_OF_RULER]->(r:ISSUER) OPTIONAL MATCH (n)-[pb:PROVIDED_BY]->(ie:ISSUING_ENTITY) OPTIONAL MATCH (n)-[ucur:UNDER_CURRENCY]->(cur:CURRENCY) OPTIONAL MATCH (n)-[ht:HAS_TYPE]->(t:TYPE) OPTIONAL MATCH (n)-[cf:COMMEMORATE_FOR]->(ce:COMMEMORATED_EVENT) OPTIONAL MATCH (n)-[hs:HAS_SERIES]->(s:SERIES) OPTIONAL MATCH (n)-[hr:HAS_REFERENCE]->(cr:CATALOGUE_REFERENCE) OPTIONAL MATCH (n)-[hc:HAS_COMPOSITION]->(comp:COMPOSITION) OPTIONAL MATCH (n)-[hsh:HAS_SHAPE]->(sh:SHAPE) OPTIONAL MATCH (n)-[wt:WITH_TECHNIQUE]->(tq:TECHNIQUE) OPTIONAL MATCH (n)-[hob:HAS_OBVERSE]->(ntpo:NTYPE_PART) OPTIONAL MATCH (n)-[hrv:HAS_REVERSE]->(ntpr:NTYPE_PART) OPTIONAL MATCH (n)-[hed:HAS_EDGE]->(ntpe:NTYPE_PART) OPTIONAL MATCH (n)-[hsm:HAS_SPECIFIED_MINT]->(sm:SPECIFIED_MINT) OPTIONAL MATCH (n)-[hw:HAS_WATERMARK]->(ntpw:NTYPE_PART) OPTIONAL MATCH (n)-[prb:PRINTED_BY]->(p:PRINTER) OPTIONAL MATCH (n)-[rw:REFERENCED_WITH]->(ntag:NTAG) OPTIONAL MATCH (n)-[dw:DATED_WITHIN]->(cal:CALENDAR) " +
//            "RETURN count(n)" +
////            "DISTINCT n, uc, c, ib, i, dor, r, pb, ie, ucur, cur, ht, t, cf, ce, hs, s, hr, cr, hc, comp, hsh, sh, wt, tq, hob, ntpo, hrv, ntpr, hed, ntpe, hsm, sm, hw, ntpw, prb, p, rw, ntag, dw, cal" +
//            "")
//    Integer findByNidAllCount();

//    @Query("MATCH (n:NTYPE) WHERE n.nid='201394' RETURN n")
//    NType findByNid(String nid);



    Boolean existsByNid(String nid);

    @Query("MATCH (n:NTYPE {nid:$nid}) SET n.isActual = toBoolean(true) return n")
    NType setActual(String nid);

    @Query("MATCH (n:NTYPE) WHERE n.isActual = toBoolean($isActual) return n")
    List<NType> findByIsActual(Boolean isActual);


    @Query("MATCH (n:NTYPE) WHERE n.title =~ $filter RETURN n ORDER BY n.name LIMIT 50")
    List<NType> findByTitleFilter(String filter);

    @Query("MATCH (c:COUNTRY {eid:$eid})-[*1..5]-(n:NTYPE) RETURN n ORDER BY n.name LIMIT 50")
    List<NType> findByCountryEid(String eid);

    @Query("MATCH (c:COUNTRY {eid:$eid})-[*1..5]-(n:NTYPE) WHERE n.title =~ $filter RETURN n ORDER BY n.name LIMIT 50")
    List<NType> findByTitleFilterAndCountryEid(String filter, String eid);

    @Query("MATCH (c:SUBJECT {eid:$eid})-[*1..5]->(:ISSUER)-[:ISSUED_BY]-(n:NTYPE) RETURN n ORDER BY n.name LIMIT 50")
    List<NType> findBySubjectEid(String eid);

    @Query("MATCH (c:SUBJECT {eid:$eid})-[*1..5]->(:ISSUER)-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT), " +
            "(n)-[ro:HAS_OBVERSE]->(o:NTYPE_PART), " +
            "(n)-[rr:HAS_REVERSE]-(r:NTYPE_PART) " +
            "WHERE (v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) " +
            "RETURN DISTINCT n, collect(ro),collect(o),collect(rr),collect(r), collect(rv), collect(v) ORDER BY n.name LIMIT 50")
    List<NType> findBySubjectEidAndYear(String eid, int year);

    @Query("MATCH (c:SUBJECT {eid:$eid})-[*1..5]->(:ISSUER)-[:ISSUED_BY]-(n:NTYPE) WHERE n.title =~ $filter RETURN n ORDER BY n.name LIMIT 50")
    List<NType> findByTitleFilterAndSubjectEid(String filter, String eid);

    @Query("MATCH (c:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE) RETURN n ORDER BY n.title")
    List<NType> findByIssuerEid(String eid);

    @Query("MATCH (c:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE) RETURN n.nid ORDER BY n.title")
    List<String> findNTypeNidByIssuerEid(String eid);

    @Query("MATCH (c:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT), " +
            "(n)-[ro:HAS_OBVERSE]->(o:NTYPE_PART), " +
            "(n)-[rr:HAS_REVERSE]-(r:NTYPE_PART) " +
            "WHERE (v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) " +
            "RETURN DISTINCT n,collect(ro),collect(o),collect(rr),collect(r),collect(rv),collect(v) ORDER BY n.title")
    List<NType> findByIssuerEidAndYear(String eid, int year);

//    @Query("MATCH (c:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT), " +
//            "(n)-[ro:HAS_OBVERSE]->(o:NTYPE_PART), " +
//            "(n)-[rr:HAS_REVERSE]-(r:NTYPE_PART) " +
//            "WHERE (v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) " +
//            "WITH DISTINCT n.title as title, count(v) as variantsCount " +
//            "RETURN title,variantsCount ORDER BY title")
//    Flux<MinMaxYears> findByIssuerEidAndYearWithVariantsCount(String eid, int year);

    @Query("MATCH (c:ISSUER {eid:$issuerEid})<-[:ISSUED_BY]-(n:NTYPE) WHERE n.title =~ $filter RETURN n ORDER BY n.name")
    List<NType> findByTitleFilterAndIssuerEid(String filter, String issuerEid);

    @Query("MATCH (c:ISSUER {eid:$issuerEid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT), " +
            "(n)-[ro:HAS_OBVERSE]->(o:NTYPE_PART), " +
            "(n)-[rr:HAS_REVERSE]-(r:NTYPE_PART)  " +
            "WHERE n.title =~ $filter AND ((v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) " +
            "RETURN DISTINCT n,ro,o,rr,r ORDER BY n.name")
    List<NType> findByTitleFilterAndIssuerEidAndYear(String filter, String issuerEid, int year);





}
