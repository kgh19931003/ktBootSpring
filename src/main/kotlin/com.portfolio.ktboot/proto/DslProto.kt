package com.portfolio.ktboot.proto

import org.jooq.*
import org.jooq.impl.TableImpl

fun <R : Record> SelectConditionStep<R>.`when`(check: () -> Boolean, condition: Condition): SelectConditionStep<R> {
    return if (check()) {
        this.and(condition)
    } else {
        this
    }
}

fun <R : Record> SelectConditionStep<R>.`when`(check: Boolean, condition: Condition): SelectConditionStep<R> {
    return if (check) {
        this.and(condition)
    } else {
        this
    }
}

fun <R : Record> SelectConditionStep<R>.ifElse(
    condition: () -> Boolean,
): (
    onTrue: (SelectConditionStep<R>) -> SelectConditionStep<R>,
) -> (onNot: (SelectConditionStep<R>) -> SelectConditionStep<R>) -> SelectConditionStep<R> {
    return { onTrue ->
        { onNot ->
            if (condition()) {
                onTrue(this)
            } else {
                onNot(this)
            }
        }
    }
}

fun <R : Record> SelectConditionStep<R>.ifThen(
    check: () -> Boolean,
    action: (SelectConditionStep<R>) -> SelectConditionStep<R>,
): SelectConditionStep<R> {
    return if (check()) {
        action(this)
    } else {
        this
    }
}

fun SelectConditionStep<*>.fieldsNot(vararg field: Field<*>): Set<Field<*>> {
    return this.fields().subtract(field.toSet())
}
fun SelectJoinStep<*>.fieldsNot(vararg field: Field<*>): Set<Field<*>> {
    return this.fields().subtract(field.toSet())
}
fun TableImpl<*>.fieldsNot(vararg field: Field<*>): Set<Field<*>> {
    return this.fields().subtract(field.toSet())
}
fun <R : Record> DSLContext.append(table: Table<R>): R {
    return newRecord(table)
}
