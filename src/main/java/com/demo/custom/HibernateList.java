package com.demo.custom;

import com.demo.utils.FieldCustomType;
import com.demo.utils.TypeImpl;
import io.github.jleblanc64.libcustom.LibCustom;
import io.vavr.control.Option;
import lombok.SneakyThrows;
import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.property.access.spi.SetterFieldImpl;
import org.hibernate.type.BagType;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static io.github.jleblanc64.libcustom.FieldMocked.getRefl;

public class HibernateList {
    public static Class<?> class_ = io.vavr.collection.List.class;

    @SneakyThrows
    public static void override() {
        LibCustom.modifyArgWithSelf(SetterFieldImpl.class, "set", 1, argsSelf -> {
            var args = argsSelf.args;
            var value = args[1];
            var self = argsSelf.self;
            var field = (Field) getRefl(self, SetterFieldImpl.class.getDeclaredField("field"));

            if (field.getType() == class_) {
                var bag = (PersistentBag) value;
                return io.vavr.collection.List.ofAll(bag);
            }

            if (field.getType() == HibernateOption.class_ && !(value instanceof Option))
                return Option.of(value);

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyArg(Class.forName("org.hibernate.annotations.common.reflection.java.JavaXProperty"), "create", 0, args -> {
            var member = args[0];
            if (member instanceof Field) {
                var field = (Field) member;
                if (!(field.getGenericType() instanceof ParameterizedType))
                    return LibCustom.ORIGINAL;

                var type = (ParameterizedType) field.getGenericType();
                var typeRaw = type.getRawType();
                var typeParam = type.getActualTypeArguments()[0];
                if (typeRaw == class_)
                    return FieldCustomType.create(field, new TypeImpl(List.class, new Type[]{typeParam}, null));

                if (typeRaw == HibernateOption.class_)
                    return FieldCustomType.create(field, new TypeImpl((Class<?>) typeParam, new Type[]{}, null));
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyReturn(Class.forName("org.hibernate.metamodel.internal.BaseAttributeMetadata"), "getJavaType", argRet -> {
            var clazz = argRet.returned;
            if (clazz == class_)
                return List.class;

            return clazz;
        });

        LibCustom.modifyArg(Class.forName("org.hibernate.type.CollectionType"), "getElementsIterator", 0, args -> {
            var collection = args[0];
            if (collection instanceof io.vavr.collection.List)
                return ((io.vavr.collection.List) collection).toJavaList();

            return collection;
        });

        LibCustom.modifyArg(BagType.class, "wrap", 1, args -> {
            var collection = args[1];
            if (collection instanceof io.vavr.collection.List)
                return ((io.vavr.collection.List) collection).toJavaList();

            return collection;
        });

        LibCustom.modifyReturn(AbstractNamedValueMethodArgumentResolver.class, "resolveArgument", argsRet -> {
            var args = argsRet.args;
            var returned = argsRet.returned;
            var type = ((MethodParameter) args[0]).getParameterType();

            if (type == Option.class && !(returned instanceof Option))
                return Option.of(returned);

            return returned;
        });

        LibCustom.modifyArg(Class.forName("org.springframework.beans.TypeConverterDelegate"), "doConvertValue", 1, args -> {
            var newValue = args[1];
            var requiredType = (Class<?>) args[2];

            if (requiredType == Option.class)
                return Option.of(newValue);

            return LibCustom.ORIGINAL;
        });
    }
}
