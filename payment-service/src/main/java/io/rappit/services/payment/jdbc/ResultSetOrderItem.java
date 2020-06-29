package io.rappit.services.payment.jdbc;

import io.rappit.common.world.finance.Money;
import io.rappit.services.payment.api.OrderItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;
import java.util.Optional;

public class ResultSetOrderItem implements OrderItem {
    private final Money price;
    private final Integer quantity;
    private final String sku;
    private final String description;
    private final Money tax;

    public ResultSetOrderItem(ResultSet resultSet) throws SQLException {
        this.price = new Money.Simple(
                resultSet.getBigDecimal("ITEM_PRICE"),
                Currency.getInstance(resultSet.getString("ITEM_CURRENCY"))
        );
        this.sku = resultSet.getString("ITEM_SKU");
        this.description = resultSet.getString("ITEM_DESCRIPTION");
        this.tax = Optional.ofNullable(resultSet.getBigDecimal("ITEM_TAX"))
                .map(tax -> new Money.Simple(
                                tax,
                                this.price.currency()
                        )
                ).orElse(null);
        this.quantity = resultSet.getInt("ITEM_QUANTITY");
    }

    @Override
    public Money price() {
        return price;
    }

    @Override
    public Integer quantity() {
        return quantity;
    }

    @Override
    public Optional<String> sku() {
        return Optional.ofNullable(sku);
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Optional<Money> tax() {
        return Optional.ofNullable(tax);
    }
}
