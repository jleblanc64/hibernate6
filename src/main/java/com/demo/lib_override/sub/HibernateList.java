package com.demo.lib_override.sub;

import com.demo.functional.ListF;
import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.property.access.spi.SetterFieldImpl;

import java.lang.reflect.Field;

import static com.demo.functional.ListF.f;
import static io.github.jleblanc64.libcustom.FieldMocked.getRefl;
import static io.github.jleblanc64.libcustom.LibCustom.modifyArgWithSelf;

public class HibernateList {
    public static void override() {
        modifyArgWithSelf(SetterFieldImpl.class, "set", 1, argsSelf -> {
            var args = argsSelf.args;
            var self = argsSelf.self;
            var field = (Field) getRefl(self, SetterFieldImpl.class.getDeclaredField("field"));

            if (field.getType() == ListF.class) {
                var bag = (PersistentBag) args[1];
                return f(bag);
            }

            return null;
        });
    }
}
