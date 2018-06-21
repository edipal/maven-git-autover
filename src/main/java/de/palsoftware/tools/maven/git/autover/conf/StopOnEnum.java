
package de.palsoftware.tools.maven.git.autover.conf;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Holds possible settings for stop on.
 */
@XmlType(name = "stopOnEnum", namespace = "http://de.palsoftware/tools/maven/git/autover/conf")
@XmlEnum
public enum StopOnEnum {

    /**
     * It will use the first branch that it will find.
     */
    ON_FIRST,
    /**
     * It will use the first annotated branch that it will find.
     */
    ON_FIRST_ANN,
    /**
     * It will use the first light branch that it will find.
     */
    ON_FIRST_LIGHT;

    /**
     * Construct from string.
     *
     * @param v the name
     * @return the enum
     */
    public static StopOnEnum fromValue(final String v) {
        return valueOf(v);
    }

    /**
     * Getter.
     *
     * @return the name
     */
    public String value() {
        return name();
    }

}
