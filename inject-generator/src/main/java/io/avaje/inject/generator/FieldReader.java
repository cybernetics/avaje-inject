package io.avaje.inject.generator;

import javax.lang.model.element.Element;

class FieldReader {

  private final Element element;
  private final String name;
  private final UtilType type;
  private boolean requestParam;
  private String requestParamName;

  FieldReader(Element element) {
    this.element = element;
    this.name = Util.getNamed(element);
    this.type = Util.determineType(element.asType());
  }

  String getFieldName() {
    return element.getSimpleName().toString();
  }

  String builderGetDependency() {
    StringBuilder sb = new StringBuilder();
    sb.append("b.").append(type.getMethod());
    sb.append(type.rawType()).append(".class");
    if (name != null) {
      sb.append(",\"").append(name).append("\"");
    }
    sb.append(")");
    return sb.toString();
  }

  /**
   * Check for request scoped dependency.
   */
  void checkRequest(BeanRequestParams requestParams) {
    requestParam = requestParams.check(type.rawType());
    if (requestParam) {
      requestParamName = requestParams.argumentName(type.rawType());
    }
  }

  /**
   * Generate code for dependency inject for BeanFactory.
   */
  void writeRequestDependency(Append writer) {
    if (!requestParam) {
      // just add as field dependency
      requestParamName = writer.nextName(getFieldName().toLowerCase());//"dep");
      final String shortType = nm(type.rawType());
      writer.append("  @Inject").eol();
      writer.append("  %s %s;", shortType, requestParamName).eol().eol();
    }
  }

  /**
   * Generate code to set bean field dependencies as part of BeanFactory create().
   */
  void writeRequestInject(Append writer) {
    writer.append("    bean.%s = %s;", getFieldName(), requestParamName).eol();
  }

  private String nm(String raw) {
    return Util.shortName(raw);
  }

}
