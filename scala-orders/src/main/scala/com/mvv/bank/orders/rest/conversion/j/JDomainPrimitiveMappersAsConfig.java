package com.mvv.bank.orders.rest.conversion.j;

import org.mapstruct.MapperConfig;



@MapperConfig (
    //componentModel = MAP_STRUCT_COMPONENT_MODEL,
    uses = {
        JDomainMappers.class,
        JEnumMappers.class,
        JOptionMapper.class,
    })
public interface JDomainPrimitiveMappersAsConfig { }
