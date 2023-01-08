package com.mvv.bank.orders.rest.conversion;

import org.mapstruct.Mapper;


// These primitive mappers are not needed for manual usage, for this reason we can put all them into
// one file with package access

@Mapper
interface CurrencyMapper extends com.mvv.bank.orders.rest.conversion.impl.CurrencyMapper {
}


@Mapper(uses = CurrencyMapper.class)
interface AmountMapper extends com.mvv.bank.orders.rest.conversion.impl.AmountMapper {
}
