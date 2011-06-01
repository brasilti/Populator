package br.com.wave.populator.fillers;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;

import br.com.wave.populator.core.Populator;
import br.com.wave.populator.exceptions.PopulatorException;

public class OneToManyFiller extends Filler {

	public OneToManyFiller(Populator populator) {
		super(populator);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void fill(Field field, T instance) throws PopulatorException {
		Class<?> klass = field.getType();
		
		if (klass.equals(Collection.class)) {			
			try {
				ParameterizedType type = (ParameterizedType) field.getGenericType();
				Class<?> typeArgument = (Class<?>) type.getActualTypeArguments()[0];
				
				field.set(instance, Arrays.asList(this.getPopulator().populate(typeArgument)));
			} catch (IllegalArgumentException e) {
				throw new PopulatorException(e.getMessage());
			} catch (IllegalAccessException e) {
				throw new PopulatorException(e.getMessage());
			}
		}else {
			this.getSuccessor().fill(field, instance);
		}
	}

}
