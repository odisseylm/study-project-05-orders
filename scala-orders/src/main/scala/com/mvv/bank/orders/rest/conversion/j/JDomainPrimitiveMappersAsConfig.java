package com.mvv.bank.orders.rest.conversion.j;

import org.mapstruct.MapperConfig;



@MapperConfig (
    uses = {
        JDomainMappers.class,
        JEnumMappers.class,
        JOptionMapper.class,
    })
public interface JDomainPrimitiveMappersAsConfig { }
