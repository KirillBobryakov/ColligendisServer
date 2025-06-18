package bkv.colligendis.utils.numista.item;

public enum ItemType {
    Coin("147"), Token("150"), Medal("149"), Banknote("148"), PaperExonumia("143");

    private final String code;

    ItemType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
