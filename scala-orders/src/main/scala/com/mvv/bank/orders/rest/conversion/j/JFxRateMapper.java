package com.mvv.bank.orders.rest.conversion.j;

import com.mvv.bank.orders.rest.conversion.FxRateMapper;
import org.mapstruct.Mapper;


@Mapper(config = JDomainPrimitiveMappersAsConfig.class)
public interface JFxRateMapper extends FxRateMapper { }
