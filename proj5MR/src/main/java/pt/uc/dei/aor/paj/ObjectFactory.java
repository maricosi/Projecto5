//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.06.16 at 11:10:29 AM BST 
//


package pt.uc.dei.aor.paj;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Jornal_QNAME = new QName("", "jornal");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JornalType }
     * 
     */
    public JornalType createJornalType() {
        return new JornalType();
    }

    /**
     * Create an instance of {@link NoticiaType }
     * 
     */
    public NoticiaType createNoticiaType() {
        return new NoticiaType();
    }

    /**
     * Create an instance of {@link CategoriaType }
     * 
     */
    public CategoriaType createCategoriaType() {
        return new CategoriaType();
    }

    /**
     * Create an instance of {@link ImageType }
     * 
     */
    public ImageType createImageType() {
        return new ImageType();
    }

    /**
     * Create an instance of {@link VideoType }
     * 
     */
    public VideoType createVideoType() {
        return new VideoType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JornalType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "jornal")
    public JAXBElement<JornalType> createJornal(JornalType value) {
        return new JAXBElement<JornalType>(_Jornal_QNAME, JornalType.class, null, value);
    }

}
