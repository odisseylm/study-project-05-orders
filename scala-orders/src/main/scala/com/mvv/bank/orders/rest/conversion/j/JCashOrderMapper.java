package com.mvv.bank.orders.rest.conversion.j;

import com.mvv.bank.orders.rest.conversion.CashOrderMapper;
import org.mapstruct.Mapper;


@Mapper(
    uses = {
            JOptionMapper.class, JFxRateMapper.class,
            JDomainMappers.class,
            JEnumMappers.class,
            JOptionMapper.class,
    }
    //config = JDomainPrimitiveMappersAsConfig.class
)
public abstract class JCashOrderMapper extends CashOrderMapper { }
