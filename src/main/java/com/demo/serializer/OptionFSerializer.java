package com.demo.serializer;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;
import io.github.jleblanc64.libcustom.functional.OptionF;

public class OptionFSerializer
        extends ReferenceTypeSerializer<OptionF<?>> // since 2.9
{
    private static final long serialVersionUID = 1L;

    /*
    /**********************************************************
    /* Constructors, factory methods
    /**********************************************************
     */

    protected OptionFSerializer(ReferenceType fullType, boolean staticTyping,
                                TypeSerializer vts, JsonSerializer<Object> ser) {
        super(fullType, staticTyping, vts, ser);
    }

    protected OptionFSerializer(OptionFSerializer base, BeanProperty property,
                                TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper,
                                Object suppressableValue, boolean suppressNulls) {
        super(base, property, vts, valueSer, unwrapper,
                suppressableValue, suppressNulls);
    }

    @Override
    protected ReferenceTypeSerializer<OptionF<?>> withResolved(BeanProperty prop,
                                                               TypeSerializer vts, JsonSerializer<?> valueSer,
                                                               NameTransformer unwrapper) {
        return new OptionFSerializer(this, prop, vts, valueSer, unwrapper,
                _suppressableValue, _suppressNulls);
    }

    @Override
    public ReferenceTypeSerializer<OptionF<?>> withContentInclusion(Object suppressableValue,
                                                                    boolean suppressNulls) {
        return new OptionFSerializer(this, _property, _valueTypeSerializer,
                _valueSerializer, _unwrapper,
                suppressableValue, suppressNulls);
    }

    /*
    /**********************************************************
    /* Abstract method impls
    /**********************************************************
     */

    @Override
    protected boolean _isValuePresent(OptionF<?> value) {
        return value.get() != null;
    }

    @Override
    protected Object _getReferenced(OptionF<?> value) {
        return value.get();
    }

    @Override
    protected Object _getReferencedIfPresent(OptionF<?> value) {
        return value.get();
    }
}
