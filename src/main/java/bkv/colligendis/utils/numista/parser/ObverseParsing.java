// package bkv.colligendis.utils.numista.parser;

// import java.util.HashMap;
// import java.util.List;
// import java.util.UUID;
// import java.util.stream.Collectors;

// import org.jsoup.nodes.Document;
// import org.jsoup.nodes.Element;

// import bkv.colligendis.database.entity.numista.NTypePart;
// import bkv.colligendis.database.service.numista.ArtistService;
// import bkv.colligendis.database.service.numista.LetteringScriptService;
// import bkv.colligendis.database.service.numista.NTypePartService;
// import bkv.colligendis.database.service.numista.NTypeService;
// import bkv.colligendis.utils.N4JUtil;
// import bkv.colligendis.utils.numista.NumistaPartParser;
// import bkv.colligendis.utils.numista.PART_TYPE;

// public class ObverseParsing extends AbstractParser {

// public ParsingResult parse(Document page, PART_TYPE partType) {

// UUID nTypePartUuid = null;
// switch (partType) {
// case OBVERSE -> {
// nTypePartUuid = nTypeService.getObverseUuid(nTypeUuid);
// if (nTypePartUuid == null) {
// nTypePartUuid = nTypePartService.save(new
// NTypePart(PART_TYPE.OBVERSE)).getUuid();
// nTypeService.setObverse(nTypeUuid, nTypePartUuid);
// }
// }
// case REVERSE -> {
// nTypePartUuid = nTypeService.getReverseUuid(nTypeUuid);
// if (nTypePartUuid == null) {
// nTypePartUuid = nTypePartService.save(new
// NTypePart(PART_TYPE.REVERSE)).getUuid();
// nTypeService.setReverse(nTypeUuid, nTypePartUuid);
// }
// }
// case EDGE -> {
// nTypePartUuid = nTypeService.getEdgeUuid(nTypeUuid);
// if (nTypePartUuid == null) {
// nTypePartUuid = nTypePartService.save(new
// NTypePart(PART_TYPE.EDGE)).getUuid();
// nTypeService.setEdge(nTypeUuid, nTypePartUuid);
// }
// }
// case WATERMARK -> {
// nTypePartUuid = nTypeService.getWatermarkUuid(nTypeUuid);
// if (nTypePartUuid == null) {
// nTypePartUuid = nTypePartService.save(new
// NTypePart(PART_TYPE.WATERMARK)).getUuid();
// nTypeService.setWatermark(nTypeUuid, nTypePartUuid);
// }
// }
// default -> {
// return ParsingResult.ERROR;
// }
// }

// parseObverseEngravers(page, nTypeUuid);
// parseObverseDesigners(page, nTypeUuid);
// parseObverseDescription(page, nTypeUuid);
// parseObverseLettering(page, nTypeUuid);
// parseObverseScripts(page, nTypeUuid);
// parseObverseUnabridgedLegend(page, nTypeUuid);
// parseObverseLetteringTranslation(page, nTypeUuid);
// parseObversePicture(page, nTypeUuid);

// return ParsingResult.CHANGED;
// }

// private static ParsingResult parseObverseEngravers(Document page, UUID
// nTypeUuid) {
// NTypeService nTypeService =
// N4JUtil.getInstance().numistaService.nTypeService;
// NTypePartService nTypePartService =
// N4JUtil.getInstance().numistaService.nTypePartService;
// ArtistService artistService =
// N4JUtil.getInstance().numistaService.artistService;

// List<String> graveursAvers =
// NumistaPartParser.getTextsSelectedOptions(page.selectFirst("#graveur_avers"));
// if (graveursAvers != null && !graveursAvers.isEmpty()) {
// UUID obverseUuid = nTypeService.getObverseUuid(nTypeUuid);
// if (obverseUuid == null) {
// obverseUuid = nTypePartService.save(new
// NTypePart(PART_TYPE.OBVERSE)).getUuid();
// nTypeService.setObverse(nTypeUuid, obverseUuid);
// }

// nTypePartService.equateEngravers(obverseUuid, graveursAvers.stream()
// .map(engraver ->
// artistService.findUuidByNid(engraver)).collect(Collectors.toList()));

// }
// return ParsingResult.CHANGED;
// }

// private static ParsingResult parseObverseDesigners(Document page, UUID
// nTypeUuid) {
// NTypeService nTypeService =
// N4JUtil.getInstance().numistaService.nTypeService;
// NTypePartService nTypePartService =
// N4JUtil.getInstance().numistaService.nTypePartService;
// ArtistService artistService =
// N4JUtil.getInstance().numistaService.artistService;

// List<String> designersAvers =
// NumistaPartParser.getTextsSelectedOptions(page.selectFirst("#designer_avers"));
// if (designersAvers != null) {
// UUID obverseUuid = nTypeService.getObverseUuid(nTypeUuid);
// if (obverseUuid == null) {
// obverseUuid = nTypePartService.save(new
// NTypePart(PART_TYPE.OBVERSE)).getUuid();
// nTypeService.setObverse(nTypeUuid, obverseUuid);
// }
// nTypePartService.equateDesigners(obverseUuid, designersAvers.stream()
// .map(designer ->
// artistService.findUuidByNid(designer)).collect(Collectors.toList()));
// }
// return ParsingResult.CHANGED;
// }

// private static ParsingResult parseObverseDescription(Document page, UUID
// nTypeUuid) {
// NTypeService nTypeService =
// N4JUtil.getInstance().numistaService.nTypeService;
// NTypePartService nTypePartService =
// N4JUtil.getInstance().numistaService.nTypePartService;

// String descriptionAvers =
// NumistaPartParser.getTagText(page.selectFirst("#description_avers"));
// if (descriptionAvers != null && !descriptionAvers.isEmpty()) {
// UUID obverseUuid = nTypeService.getObverseUuid(nTypeUuid);
// if (obverseUuid == null) {
// obverseUuid = nTypePartService.save(new
// NTypePart(PART_TYPE.OBVERSE)).getUuid();
// nTypeService.setObverse(nTypeUuid, obverseUuid);
// }

// if (!nTypePartService.compareDescription(obverseUuid, descriptionAvers)) {
// nTypePartService.setDescription(obverseUuid, descriptionAvers);
// }
// }
// return ParsingResult.CHANGED;
// }

// private static ParsingResult parseObverseLettering(Document page, UUID
// nTypeUuid) {
// ParsingResult result = ParsingResult.NOT_CHANGED;
// NTypeService nTypeService =
// N4JUtil.getInstance().numistaService.nTypeService;
// NTypePartService nTypePartService =
// N4JUtil.getInstance().numistaService.nTypePartService;

// String texteAvers =
// NumistaPartParser.getTagText(page.selectFirst("#texte_avers"));
// if (texteAvers != null && !texteAvers.isEmpty()) {
// UUID obverseUuid = nTypeService.getObverseUuid(nTypeUuid);
// if (obverseUuid == null) {
// obverseUuid = nTypePartService.save(new
// NTypePart(PART_TYPE.OBVERSE)).getUuid();
// nTypeService.setObverse(nTypeUuid, obverseUuid);
// }

// if (!nTypePartService.compareLettering(obverseUuid, texteAvers)) {
// nTypePartService.setLettering(obverseUuid, texteAvers);
// }
// }
// return result;
// }

// private static ParsingResult parseObverseScripts(Document page, UUID
// nTypeUuid) {
// ParsingResult result = ParsingResult.NOT_CHANGED;
// NTypeService nTypeService =
// N4JUtil.getInstance().numistaService.nTypeService;
// NTypePartService nTypePartService =
// N4JUtil.getInstance().numistaService.nTypePartService;
// LetteringScriptService letteringScriptService =
// N4JUtil.getInstance().numistaService.letteringScriptService;

// List<HashMap<String, String>> scriptsAvers =
// NumistaPartParser.getAttributesWithTextSelectedOptions(
// page.selectFirst("#script_avers"));
// if (scriptsAvers != null && !scriptsAvers.isEmpty()) {

// UUID obverseUuid = nTypeService.getObverseUuid(nTypeUuid);
// if (obverseUuid == null) {
// obverseUuid = nTypePartService.save(new
// NTypePart(PART_TYPE.OBVERSE)).getUuid();
// nTypeService.setObverse(nTypeUuid, obverseUuid);
// }

// nTypePartService.equateLetteringScripts(obverseUuid, scriptsAvers.stream()
// .map(script -> letteringScriptService
// .findUuidByNid(script.get("value")))
// .collect(Collectors.toList()));

// }

// return result;
// }

// private static ParsingResult parseObverseUnabridgedLegend(Document page, UUID
// nTypeUuid) {
// ParsingResult result = ParsingResult.NOT_CHANGED;
// NTypeService nTypeService =
// N4JUtil.getInstance().numistaService.nTypeService;
// NTypePartService nTypePartService =
// N4JUtil.getInstance().numistaService.nTypePartService;

// String unabridgedAvers =
// NumistaPartParser.getTagText(page.selectFirst("#unabridged_avers"));
// if (unabridgedAvers != null && !unabridgedAvers.isEmpty()) {
// UUID obverseUuid = nTypeService.getObverseUuid(nTypeUuid);
// if (obverseUuid == null) {
// obverseUuid = nTypePartService.save(new
// NTypePart(PART_TYPE.OBVERSE)).getUuid();
// nTypeService.setObverse(nTypeUuid, obverseUuid);
// }

// if (!nTypePartService.compareUnabridgedLegend(obverseUuid, unabridgedAvers))
// {
// nTypePartService.setUnabridgedLegend(obverseUuid, unabridgedAvers);
// }

// result = ParsingResult.CHANGED;
// }
// return result;
// }

// private static ParsingResult parseObverseLetteringTranslation(Document page,
// UUID nTypeUuid) {
// ParsingResult result = ParsingResult.NOT_CHANGED;
// NTypeService nTypeService =
// N4JUtil.getInstance().numistaService.nTypeService;
// NTypePartService nTypePartService =
// N4JUtil.getInstance().numistaService.nTypePartService;

// String traductionAvers =
// NumistaPartParser.getTagText(page.selectFirst("#traduction_avers"));
// if (traductionAvers != null && !traductionAvers.isEmpty()) {
// UUID obverseUuid = nTypeService.getObverseUuid(nTypeUuid);
// if (obverseUuid == null) {
// obverseUuid = nTypePartService.save(new
// NTypePart(PART_TYPE.OBVERSE)).getUuid();
// nTypeService.setObverse(nTypeUuid, obverseUuid);
// }

// if (!nTypePartService.compareLetteringTranslation(obverseUuid,
// traductionAvers)) {
// nTypePartService.setLetteringTranslation(obverseUuid, traductionAvers);
// }

// result = ParsingResult.CHANGED;
// }
// return result;
// }

// private static ParsingResult parseObversePicture(Document page, UUID
// nTypeUuid) {
// ParsingResult result = ParsingResult.NOT_CHANGED;
// NTypeService nTypeService =
// N4JUtil.getInstance().numistaService.nTypeService;
// NTypePartService nTypePartService =
// N4JUtil.getInstance().numistaService.nTypePartService;

// Element obverse = page.selectFirst("fieldset:contains(Obverse)");
// if (obverse != null) {
// String obversePhoto =
// NumistaPartParser.getAttribute(obverse.selectFirst("a[target=_blank]"),
// "href");
// if (obversePhoto != null && !obversePhoto.isEmpty()) {
// UUID obverseUuid = nTypeService.getObverseUuid(nTypeUuid);
// if (obverseUuid == null) {
// obverseUuid = nTypePartService.save(new
// NTypePart(PART_TYPE.OBVERSE)).getUuid();
// nTypeService.setObverse(nTypeUuid, obverseUuid);
// }
// if (!nTypePartService.comparePicture(obverseUuid, obversePhoto)) {
// nTypePartService.setPicture(obverseUuid, obversePhoto);
// }

// result = ParsingResult.CHANGED;
// }
// }
// return result;
// }

// }
