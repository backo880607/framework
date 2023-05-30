package com.pisces.framework.core.validator.impl;

import com.pisces.framework.core.entity.BaseObject;
import com.pisces.framework.core.validator.constraints.PrimaryKey;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 主键验证器
 *
 * @author jason
 * @date 2022/12/08
 */
public class PrimaryKeyValidator implements ConstraintValidator<PrimaryKey, BaseObject> {

    @Override
    public boolean isValid(BaseObject value, ConstraintValidatorContext context) {
        return true;
		/*List<Property> properties = AppUtils.getPropertyService().getPrimaries(value.getClass());
		String key = StringUtils.join(properties, "\t", (Property property) -> {
			return EntityUtils.getTextValue(value, property, null);
		});
		
		Set<String> primaryKeys = new HashSet<>();
		EntityService<? extends EntityObject> service = ServiceManager.getService(value.getClass());
		if (service != null) {
			List<? extends EntityObject> entities = service.getAll();
			for (EntityObject bean : entities) {
				String temp = StringUtils.join(properties, "\t", (Property property) -> {
					return EntityUtils.getTextValue(bean, property, null);
				});
				
				primaryKeys.add(temp);
			}
		}
		
		return !primaryKeys.contains(key);*/
    }
}
