//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.06.11 at 05:08:57 PM BST 
//


package pt.uc.dei.aor.paj;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for categoria_tipo.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="categoria_tipo"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="europe"/&gt;
 *     &lt;enumeration value="us"/&gt;
 *     &lt;enumeration value="asia"/&gt;
 *     &lt;enumeration value="africa"/&gt;
 *     &lt;enumeration value="china"/&gt;
 *     &lt;enumeration value="americas"/&gt;
 *     &lt;enumeration value="middleast"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "categoria_tipo")
@XmlEnum
public enum CategoriaNome {

    @XmlEnumValue("europe")
    EUROPE("europe"),
    @XmlEnumValue("us")
    US("us"),
    @XmlEnumValue("asia")
    ASIA("asia"),
    @XmlEnumValue("africa")
    AFRICA("africa"),
    @XmlEnumValue("china")
    CHINA("china"),
    @XmlEnumValue("americas")
    AMERICAS("americas"),
    @XmlEnumValue("middleast")
    MIDDLEAST("middleast");
    private final String value;

    CategoriaNome(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CategoriaNome fromValue(String v) {
        for (CategoriaNome c: CategoriaNome.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
