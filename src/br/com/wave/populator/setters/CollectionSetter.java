package br.com.wave.populator.setters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.brasilti.utils.collection.CollectionUtil;
import br.com.brasilti.utils.reflection.ReflectionUtil;
import br.com.wave.populator.core.Filler;
import br.com.wave.populator.exceptions.PopulatorException;

public class CollectionSetter extends Setter {

	public CollectionSetter(Filler filler) {
		super(filler);
	}

	@Override
	public <T> void set(T instance) throws PopulatorException {
		List<Field> fields = ReflectionUtil.getPersistentFields(instance.getClass());
		for (Field field : fields) {
			Class<?> klass = field.getType();
			boolean isCollection = ReflectionUtil.isCollection(klass);

			if (isCollection) {
				Collection<?> elements = (Collection<?>) ReflectionUtil.get(field, instance);

				if (elements == null || elements.isEmpty()) {
					Object value = this.getValueOfElement(field);
					ReflectionUtil.set(CollectionUtil.convert(value), field, instance);
				} else {
					List<Object> targetCollection = new ArrayList<Object>();

					for (Object element : elements) {
						if (element == null) {
							element = this.getValueOfElement(field);
						} else {
							this.getFiller().fill(element);
						}

						targetCollection.add(element);
					}

					ReflectionUtil.set(targetCollection, field, instance);
				}
			}
		}

		this.getSuccessor().set(instance);
	}

	private <T> Object getValueOfElement(Field field) throws PopulatorException {
		Object value = null;

		try {
			Class<?> typeOfElements = ReflectionUtil.getTypeOfElements(field);
			if (this.getManager().hasPattern(typeOfElements)) {
				value = this.getManager().getValue(typeOfElements);
			} else {
				value = typeOfElements.newInstance();
				this.getFiller().fill(value);
			}
		} catch (InstantiationException e) {
			throw new PopulatorException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new PopulatorException(e.getMessage());
		}

		return value;
	}

}
