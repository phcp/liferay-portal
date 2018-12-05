package com.liferay.forms.apio.internal.architect.form;

import com.liferay.apio.architect.form.Form;
import com.liferay.media.object.apio.architect.identifier.MediaObjectIdentifier;

public class FieldValueForm {

	public static Form<FieldValueForm> buildForm(
		Form.Builder<FieldValueForm> builder) {

		return builder.title(
			__ -> "The field value form"
		).description(
			__ -> "This form is used to create the value of a field from a form"
		).constructor(
			FieldValueForm::new
		).addOptionalLinkedModel(
			"document", MediaObjectIdentifier.class,
			FieldValueForm::setDocument
		).addRequiredString(
			"name", FieldValueForm::setName
		).addOptionalString(
			"value", FieldValueForm::setValue
		).build();
	}

	public Long getDocument() {
		return _document;
	}

	public String getName() {
		return _name;
	}

	public String getValue() {
		return _value;
	}

	public void setDocument(Long document) {
		_document = document;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setValue(String value) {
		_value = value;
	}

	private Long _document;
	private String _name;
	private String _value;

}
