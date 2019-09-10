package io.izzel.aaa.command;

import com.google.inject.Inject;
import io.izzel.aaa.AmberAdvancedAttributes;
import io.izzel.aaa.command.elements.IndexValueElement;
import io.izzel.aaa.service.Attribute;
import io.izzel.aaa.util.DataUtil;
import io.izzel.amber.commons.i18n.AmberLocale;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.NoSuchElementException;
import java.util.Optional;

class NonMarkerValueCommand {

    @Inject private AmberLocale locale;

    <T extends DataSerializable> CommandCallable prepend(String id, Attribute<T> attribute, CommandElement valueElement) {
        return CommandSpec.builder()
                .arguments(valueElement)
                .executor((src, args) -> {
                    if (src instanceof Player) {
                        Optional<ItemStack> stackOptional = ((Player) src).getItemInHand(HandTypes.MAIN_HAND);
                        Optional<T> rangeValueOptional = args.getOne(valueElement.getKey());
                        if (stackOptional.isPresent() && rangeValueOptional.isPresent()) {
                            ItemStack stack = stackOptional.get();
                            if (DataUtil.hasData(stack)) {
                                attribute.prependValue(stack, rangeValueOptional.get());
                                ((Player) src).setItemInHand(HandTypes.MAIN_HAND, stack);
                                this.locale.to(src, "commands.range.prepend-attribute", stack, id);
                                return CommandResult.success();
                            }
                        }
                    }
                    this.locale.to(src, "commands.drop.nonexist");
                    return CommandResult.success();
                })
                .build();
    }

    /**
     * get the append command for a attribute value to sth.
     *
     * @param id           the id of the attribute
     * @param attribute    the attribute value
     * @param valueElement the command value element
     * @param <T>          the type of the value
     * @return the command to append the value to the item stack.
     */
    <T extends DataSerializable> CommandCallable append(String id, Attribute<T> attribute, CommandElement valueElement) {
        return CommandSpec.builder()
                .arguments(valueElement)
                .executor((src, args) -> {
                    if (src instanceof Player) {
                        Optional<ItemStack> stackOptional = ((Player) src).getItemInHand(HandTypes.MAIN_HAND);
                        Optional<T> rangeValueOptional = args.getOne(valueElement.getKey());
                        if (stackOptional.isPresent() && rangeValueOptional.isPresent()) {
                            ItemStack stack = stackOptional.get();
                            if (DataUtil.hasData(stack)) {
                                attribute.appendValue(stack, rangeValueOptional.get());
                                ((Player) src).setItemInHand(HandTypes.MAIN_HAND, stack);
                                this.locale.to(src, "commands.range.append-attribute", stack, id);
                                return CommandResult.success();
                            }
                        }
                    }
                    this.locale.to(src, "commands.drop.nonexist");
                    return CommandResult.success();
                })
                .build();
    }

    /**
     * get the insert command for a attribute value to sth.
     *
     * @param id           the id of the attribute
     * @param attribute    the attribute
     * @param valueElement the command value element
     * @param <T>          the type of the value
     * @return the command to insert the value
     */
    <T extends DataSerializable> CommandCallable insert(String id, Attribute<T> attribute, CommandElement valueElement) {
        return CommandSpec.builder()
                .arguments(valueElement, new IndexValueElement(this.locale, Text.of("index")))
                .executor((src, args) -> {
                    if (src instanceof Player) {
                        int index = args.<Integer>getOne(Text.of("index")).orElseThrow(NoSuchElementException::new);
                        Optional<ItemStack> stackOptional = ((Player) src).getItemInHand(HandTypes.MAIN_HAND);
                        Optional<T> rangeValueOptional = args.getOne(valueElement.getKey());
                        if (stackOptional.isPresent() && rangeValueOptional.isPresent()) {
                            ItemStack stack = stackOptional.get();
                            if (DataUtil.hasData(stack)) {
                                attribute.insertValue(stack, index, rangeValueOptional.get());
                                ((Player) src).setItemInHand(HandTypes.MAIN_HAND, stack);
                                this.locale.to(src, "commands.range.append-attribute", stack, id);
                                return CommandResult.success();
                            }
                        }
                    }
                    this.locale.to(src, "commands.drop.nonexist");
                    return CommandResult.success();
                })
                .build();
    }

    <T extends DataSerializable> CommandCallable clear(String id, Attribute<T> attribute) {
        return CommandSpec.builder()
                .executor((src, args) -> {
                    if (src instanceof Player) {
                        Optional<ItemStack> stackOptional = ((Player) src).getItemInHand(HandTypes.MAIN_HAND);
                        if (stackOptional.isPresent()) {
                            ItemStack stack = stackOptional.get();
                            if (DataUtil.hasData(stack)) {
                                attribute.clearValues(stack);
                                ((Player) src).setItemInHand(HandTypes.MAIN_HAND, stack);
                                this.locale.to(src, "commands.range.clear-attribute", stack, id);
                                return CommandResult.success();
                            }
                        }
                    }
                    this.locale.to(src, "commands.drop.nonexist");
                    return CommandResult.success();
                })
                .build();
    }

    <T extends DataSerializable> CommandCallable callable(Attribute<T> attribute, String id, CommandElement element) {
        return CommandSpec.builder()
                .permission(AmberAdvancedAttributes.ID + ".command.aaa-" + id)
                .child(append(id, attribute, element), "append")
                .child(prepend(id, attribute, element), "prepend")
                .child(clear(id, attribute), "clear")
                .child(insert(id, attribute, element), "insert")
                .build();
    }
}
