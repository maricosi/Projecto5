//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.06.16 at 07:46:13 AM BST 
//


package other;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for categoria_tipo.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="categoria_tipo">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="europe"/>
 *     &lt;enumeration value="us"/>
 *     &lt;enumeration value="asia"/>
 *     &lt;enumeration value="africa"/>
 *     &lt;enumeration value="china"/>
 *     &lt;enumeration value="americas"/>
 *     &lt;enumeration value="middleast"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "categoria_tipo")
@XmlEnum
public enum CategoriaTipo {

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

    CategoriaTipo(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CategoriaTipo fromValue(String v) {
        for (CategoriaTipo c: CategoriaTipo.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
