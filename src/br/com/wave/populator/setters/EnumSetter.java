package br.com.wave.populator.setters;

import java.lang.reflect.Field;
import java.util.List;

import br.com.brasilti.utils.reflection.ReflectionUtil;
import br.com.wave.populator.core.Filler;
import br.com.wave.populator.enums.ErrorEnum;
import br.com.wave.populator.exceptions.PopulatorException;

public class EnumSetter extends Setter {

	public EnumSetter(Filler filler) {
		super(filler);
	}

	@Override
	public <T> void set(T instance) throws PopulatorException {
		List<Field> fields = ReflectionUtil.getPersistentFields(instance.getClass());
		for (Field field : fields) {
			Class<?> klass = field.getType();
			boolean isEnum = klass.isEnum();
			boolean isNull = ReflectionUtil.get(field, instance) == null;

			if (isEnum && isNull) {
				Object[] constants = klass.getEnumConstants();

				if (constants.length == 0) {
					throw new PopulatorException(ErrorEnum.EMPTY_ENUM, klass.getName());
				}

				ReflectionUtil.set(constants[0], field, instance);
			}
		}

		this.getSuccessor().set(instance);
	}

}
