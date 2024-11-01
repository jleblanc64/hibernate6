package com.demo.custom;

import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.functional.OptionF;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.type.descriptor.java.spi.UnknownBasicJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.reflections.Reflections;

import static io.github.jleblanc64.libcustom.functional.ListF.f;
import static io.github.jleblanc64.libcustom.functional.OptionF.o;

public class HibernateOption {
    public static void override() {
        // replace with your own values
        var rootPackage = "com.demo";
        var optionClass = OptionF.class;

        var tableToEntity = f(new Reflections(rootPackage).getTypesAnnotatedWith(Entity.class))
                .toMap(x -> x.getAnnotation(Table.class).name(), x -> x);

        LibCustom.override(UnknownBasicJavaType.class, "getRecommendedJdbcType", args -> {
            var context = args[0];
            if (!(context instanceof BasicValue))
                return LibCustom.ORIGINAL;

            var b = (BasicValue) context;

            var s = b.getColumn();
            if (!(s instanceof Column))
                return LibCustom.ORIGINAL;

            var c = (Column) s;

            var entity = tableToEntity.get(b.getTable().getName());
            var fields = f(entity.getDeclaredFields());
            var field = fields.findSafe(x -> x.getName().equals(c.getName()));
            if (field.getType() != optionClass)
                return LibCustom.ORIGINAL;

            return new VarcharJdbcType();
        });

        LibCustom.override(UnknownBasicJavaType.class, "unwrap", args -> {
            var v = args[0];
            var type = (Class<?>) args[1];

            OptionF<?> o;
            if (type == String.class && instanceOf(v, optionClass)) {

                o = (OptionF<?>) v;
                if (o.isPresent())
                    return o.get();

                return null;
            }

            if (type == optionClass && !instanceOf(v, optionClass))
                // return Option from nullable value v
                return o(v);

            return v;
        });

        LibCustom.overrideWithSelf(UnknownBasicJavaType.class, "wrap", argsSelf -> {
            var args = argsSelf.args;
            var u = (UnknownBasicJavaType) argsSelf.self;
            var v = args[0];
            var type = u.getJavaTypeClass();

            if (type == String.class && instanceOf(v, optionClass)) {
                var o = (OptionF<?>) v;
                if (o.isPresent())
                    return o.get();

                return null;
            }

            if (type == optionClass && !instanceOf(v, optionClass))
                // return Option from nullable value v
                return o(v);

            return v;
        });
    }

    static boolean instanceOf(Object o, Class<?> c) {
        return o != null && o.getClass() == c;
    }
}
