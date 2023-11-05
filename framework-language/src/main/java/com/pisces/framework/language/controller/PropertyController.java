package com.pisces.framework.language.controller;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.EnumDto;
import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.service.PropertyService;
import com.pisces.framework.core.utils.lang.ObjectUtils;
import com.pisces.framework.language.config.LanguageConstant;
import com.pisces.framework.type.annotation.TableMeta;
import com.pisces.framework.web.controller.BeanController;
import com.pisces.framework.web.controller.ResponseData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 财产控制器
 *
 * @author jason
 * @date 2022/12/07
 */
@RestController
@RequestMapping(LanguageConstant.IDENTIFY + "/Property")
public class PropertyController extends BeanController<Property, PropertyService> {

    @GetMapping("/listByBean")
    public ResponseData listByBean(String beanName) {
        return success(getService().get(ObjectUtils.fetchBeanClass(beanName)));
    }

    @GetMapping("/getByCode")
    public ResponseData getByCode(String beanName, String code) {
        return success(getService().get(ObjectUtils.fetchBeanClass(beanName), code));
    }

    @GetMapping("/getPrimaries")
    public ResponseData getPrimaries(String beanName) {
        return success(getService().getPrimaries(ObjectUtils.fetchBeanClass(beanName)));
    }

    @GetMapping("/getEntities")
    public ResponseData getEntities() {
        List<EnumDto> result = new ArrayList<>();
        List<Class<? extends BeanObject>> beanClasses = new LinkedList<>(ObjectUtils.getBeanClasses());
        beanClasses.sort(Comparator.comparing(Class::getSimpleName));
        for (Class<? extends BeanObject> beanClass : beanClasses) {
            TableMeta tableMeta = beanClass.getAnnotation(TableMeta.class);
            if (tableMeta != null && tableMeta.customize()) {
                EnumDto dto = new EnumDto();
                dto.setCode(beanClass.getSimpleName());
                dto.setLabel(lang.get(beanClass));
                result.add(dto);
            }
        }
        return success(result);
    }
}
