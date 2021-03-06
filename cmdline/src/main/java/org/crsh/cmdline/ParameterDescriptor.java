/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.crsh.cmdline;

import org.crsh.cmdline.binding.TypeBinding;
import org.crsh.cmdline.spi.Completer;
import org.crsh.cmdline.spi.Value;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ParameterDescriptor<B extends TypeBinding> {

  /** . */
  private final B binding;

  /** . */
  private final Description description;

  /** . */
  private final SimpleValueType type;

  /** . */
  private final Multiplicity multiplicity;

  /** . */
  private final boolean required;

  /** . */
  private final boolean password;

  /** . */
  private final Type javaType;

  /** . */
  private final Class<?> javaValueType;

  /** . */
  private final Class<? extends Completer> completerType;

  /** The annotation when it exists.  */
  private final Annotation annotation;

  /** . */
  private final boolean unquote;

  /** . */
  CommandDescriptor<?, B> owner;

  public ParameterDescriptor(
    B binding,
    Type javaType,
    Description description,
    boolean required,
    boolean password,
    boolean unquote,
    Class<? extends Completer> completerType,
    Annotation annotation) throws IllegalValueTypeException, IllegalParameterException {

    //
    Class<?> javaValueType;
    Multiplicity multiplicity;
    if (javaType instanceof Class<?>) {
      javaValueType = (Class<Object>)javaType;
      if (required) {
        multiplicity = Multiplicity.ONE;
      } else {
        multiplicity = Multiplicity.ZERO_OR_ONE;
      }
    } else if (javaType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)javaType;
      Type rawType = parameterizedType.getRawType();
      if (rawType instanceof Class<?>) {
        Class<?> classRawType = (Class<Object>)rawType;
        if (List.class.equals(classRawType)) {
          Type elementType = parameterizedType.getActualTypeArguments()[0];
          if (elementType instanceof Class<?>) {
            javaValueType = (Class<Object>)elementType;
            multiplicity = Multiplicity.ZERO_OR_MORE;
          } else {
            throw new IllegalValueTypeException();
          }
        } else {
          throw new IllegalValueTypeException();
        }
      } else {
        throw new IllegalValueTypeException();
      }
    } else {
      throw new IllegalValueTypeException();
    }

    //
    SimpleValueType valueType;
    if (javaValueType == String.class) {
      valueType = SimpleValueType.STRING;
    } else if (javaValueType == Integer.class || javaValueType == int.class) {
      valueType = SimpleValueType.INTEGER;
    } else if (javaValueType == Boolean.class || javaValueType == boolean.class) {
      valueType = SimpleValueType.BOOLEAN;
    } else if (Enum.class.isAssignableFrom(javaValueType)) {
      valueType = SimpleValueType.ENUM;
    } else if (Value.class.isAssignableFrom(javaValueType)) {
      valueType = SimpleValueType.VALUE;
    } else {
      throw new IllegalValueTypeException("Type " + javaValueType.getName() + " is not handled at the moment");
    }

    //
    if (completerType == EmptyCompleter.class) {
      completerType = valueType.getCompleter();
    }

    //
    this.binding = binding;
    this.javaType = javaType;
    this.description = description;
    this.type = valueType;
    this.multiplicity = multiplicity;
    this.required = required;
    this.password = password;
    this.completerType = completerType;
    this.annotation = annotation;
    this.javaValueType = javaValueType;
    this.unquote = unquote;
  }

  public Object parse(String s) throws Exception {
    return type.parse(javaValueType, s);
  }

  public CommandDescriptor<?, B> getOwner() {
    return owner;
  }

  public Type getJavaType() {
    return javaType;
  }

  public Class<?> getJavaValueType() {
    return javaValueType;
  }

  public final B getBinding() {
    return binding;
  }

  public final String getUsage() {
    return description != null ? description.getUsage() : "";
  }

  public Description getDescription() {
    return description;
  }

  public Annotation getAnnotation() {
    return annotation;
  }

  public final boolean isRequired() {
    return required;
  }

  public boolean isUnquote() {
    return unquote;
  }

  public final boolean isPassword() {
    return password;
  }

  public final SimpleValueType getType() {
    return type;
  }

  public final Multiplicity getMultiplicity() {
    return multiplicity;
  }

  public final Class<? extends Completer> getCompleterType() {
    return completerType;
  }

  public abstract void printUsage(Appendable writer) throws IOException;
}