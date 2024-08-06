package com.srivastavavivekggn.scala.util.web.context;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * Components annotated with ConditionalOnProperty will be registered in the spring context depending on the value of a
 * property defined in the propertiesBeanName properties Bean.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnPropertyCondition.class)
public @interface ConditionalOnProperty {

    /**
     * The name of the property. If not found, it will evaluate to false.
     */
    String value();

    /**
     * The string representation of the expected value for the properties. If not
     * specified, the property must <strong>not</strong> be equals to {@code false}.
     *
     * @return the expected value
     */
    String havingValue() default "true";

    /**
     * Specify if the condition should match if the property is not set. Defaults to
     * {@code false}.
     *
     * @return if should match if the property is missing
     */
    boolean matchIfMissing() default true;
}

/**
 * Condition that matches on the value of a property.
 *
 * @see ConditionalOnProperty
 */
class OnPropertyCondition implements ConfigurationCondition {

    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        final Map attributes = metadata.getAnnotationAttributes(ConditionalOnProperty.class.getName());
        final String propertyName = (String) attributes.get("value");
        final String expectedValue = (String) attributes.get("havingValue");
        final boolean matchIfMissing = (Boolean) attributes.get("matchIfMissing");

        String foundValue = context.getEnvironment().getProperty(propertyName, matchIfMissing ? expectedValue : "");

        return expectedValue.equals(foundValue);
    }

    /**
     * Set the registration to REGISTER, else it is handled during  the parsing of the configuration file
     * and we have no guarantee that the properties bean is loaded/exposed yet
     */
    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.REGISTER_BEAN;
    }
}