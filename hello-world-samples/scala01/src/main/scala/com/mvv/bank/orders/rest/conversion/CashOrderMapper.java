package com.mvv.bank.orders.rest.conversion;

import org.mapstruct.Mapper;


@Mapper(uses = {CurrencyMapper.class, AmountMapper.class, OptionMapper.class})
public interface CashOrderMapper
        extends com.mvv.bank.orders.rest.conversion.impl.CashOrderMapper {
}
