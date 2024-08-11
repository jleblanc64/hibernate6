package com.demo.lib_override.ser;

import com.demo.functional.OptionF;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import static com.demo.functional.OptionF.o;

final class OptionFDeserializer
        extends ReferenceTypeDeserializer<OptionF<?>> {
    private static final long serialVersionUID = 1L;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    /**
     * @since 2.9
     */
    public OptionFDeserializer(JavaType fullType, ValueInstantiator inst,
                               TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        super(fullType, inst, typeDeser, deser);
    }

    /*
    /**********************************************************
    /* Abstract method implementations
    /**********************************************************
     */

    @Override
    public OptionFDeserializer withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser) {
        return new OptionFDeserializer(_fullType, _valueInstantiator,
                typeDeser, valueDeser);
    }

    @Override
    public OptionF<?> getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        // 07-May-2019, tatu: [databind#2303], needed for nested ReferenceTypes
        return o(_valueDeserializer.getNullValue(ctxt));
    }

    @Override
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        // 07-May-2019, tatu: I _think_ this needs to align with "null value" and
        //    not necessarily with empty value of contents? (used to just do "absent"
        //    so either way this seems to me like an improvement)
        return getNullValue(ctxt);
    }

    @Override
    public OptionF<?> referenceValue(Object contents) {
        return o(contents);
    }

    @Override
    public Object getReferenced(OptionF<?> reference) {
        // 23-Apr-2021, tatu: [modules-java8#214] Need to support empty
        //    for merging too
        return reference.orElse(null);
    }

    @Override // since 2.9
    public OptionF<?> updateReference(OptionF<?> reference, Object contents) {
        return o(contents);
    }
}