package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RulerService extends AbstractService<Ruler, RulerRepository> {

    public RulerService(RulerRepository repository) {
        super(repository);
    }



    public void deleteAll(){
        repository.deleteAll();
    }


    /**
     * Find Ruler by {@code nid}.
     *
     * @param nid Ruler's nid (Numista Ruler id)
     * @return If Ruler with {@code nid} exists, then return Ruler
     */
    public Ruler findByNid(String nid) {
        return repository.findByNid(nid);
    }


    /**
     * Get a status of Ruler - actual or not.
     * This field uses while parsing new rulers by <a href="https://en.numista.com/catalogue/get_rulers.php?country=...">...</a>
     * Also, it can be uses at the start of the year to extend the ruling period
     *
     * @param uuid Ruler's uuid
     * @return tru - if actual, or false - if not actual. Also can return null, if the field .isActual is missing
     */
    public Boolean isActual(UUID uuid) {
        return repository.isActual(uuid.toString());
    }



    /**
     * Check has a Ruler any Period which starts from Year
     * @param rulerUuid Ruler's UUID
     * @param yearUuid Year's UUID
     * @return if period was found then return true
     */
    public Boolean hasRulerPeriodStartsFromYear(UUID rulerUuid, UUID yearUuid){
        return repository.hasRulerPeriodStartsFromYear(rulerUuid.toString(), yearUuid.toString());
    }

    /**
     * Check has a Ruler any Period which ends till Year
     * @param rulerUuid Ruler's UUID
     * @param yearUuid Year's UUID
     * @return if period was found then return true
     */
    public Boolean hasRulerPeriodEndsTillYear(UUID rulerUuid, UUID yearUuid){
        return repository.hasRulerPeriodEndsTillYear(rulerUuid.toString(), yearUuid.toString());
    }


    /**
     * Get Ruler's Name by UUID
     * @param rulerUuid Ruler' UUID
     * @return Ruler's Name
     */
    public String getRulerNameByUuid(UUID rulerUuid){
        return repository.getRulerNameByUuid(rulerUuid.toString());
    }



    /**
     * Create relationship [:DURING_PERIOD] from Ruler to the Period
     * @param rulerUuid Ruler's UUID
     * @param periodUuid Period's UUID
     * @return if relationship was created then return true
     */
    public Boolean setRulerDuringPeriod(UUID rulerUuid, UUID periodUuid){
        return repository.setRulerDuringPeriod(rulerUuid.toString(), periodUuid.toString());
    }



    public Ruler update(Ruler ruler, String nid, String name) {
        if (ruler == null || !ruler.getNid().equals(nid)) {
//            ruler = repository.findByNid(nid);
        }
        if (ruler != null) {
            if (!ruler.getName().equals(name)) {
                DebugUtil.showServiceMessage(this, "Trying to find Ruller with nid=" + nid + " and name=" + name
                        + ". But there is a Ruller with the same nid and other name = " + ruler.getName() + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "Ruler.name was updated.");
                ruler.setName(name);
                return repository.save(ruler);
            }
        } else {
            DebugUtil.showInfo(this, "New Ruler with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new Ruler(nid, name));
        }
        return ruler;
    }


    public List<Ruler> findRulesByIssuer(Issuer issuer){
        return repository.findByIssuerCode(issuer.getCode());
    }

}
