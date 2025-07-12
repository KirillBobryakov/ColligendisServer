package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Variant;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface VariantRepository extends AbstractNeo4jRepository<Variant> {

   Variant findByNid(String nid);

   @Query("MATCH (i:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT) " +
         "WHERE v.gregorianYear IS NOT NULL " +
         "RETURN DISTINCT v.gregorianYear")
   List<Integer> getYearsOfVariantsByIssuerEid(String eid);

   @Query("MATCH (i:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT) " +
         "WHERE v.minYear IS NOT NULL OR v.maxYear IS NOT NULL " +
         "RETURN DISTINCT v " +
         "ORDER BY v.minYear")
   List<Variant> getBetweenMinMaxYearsOfVariantsByIssuerEid(String eid);

   // @Query("MATCH (n:NTYPE
   // {nid:$nid})-[rv:VARIANTS]->(v:VARIANT)-[ma:MILLESIME_AT]->(y:YEAR) " +
   // "MATCH (v)-[mf:MILLESIME_FROM]->(yf:YEAR) " +
   // "RETURN DISTINCT v,ma,y,mf,yf " +
   // "ORDER BY y.gregorianYear")
   @Query("MATCH (n:NTYPE {nid:$nid})-[rv:VARIANTS]->(v:VARIANT) " +
         "OPTIONAL MATCH (v)-[ma:MILLESIME_AT]->(y:YEAR) " +
         "OPTIONAL MATCH (v)-[mf:MILLESIME_FROM]->(yf:YEAR) " +
         "OPTIONAL MATCH (v)-[mt:MILLESIME_TILL]->(yt:YEAR) " +
         "RETURN DISTINCT v,ma,y,mf,yf,mt,yt ")
   List<Variant> getVariantsByNTypeNid(String nid);
}
