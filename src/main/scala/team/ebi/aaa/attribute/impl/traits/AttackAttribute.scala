package team.ebi.aaa.attribute.impl.traits

import java.util.Collections
import java.util.function.DoubleUnaryOperator

import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.projectile.Projectile
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource
import org.spongepowered.api.event.cause.entity.damage.{DamageModifier, DamageModifierType, DamageModifierTypes}
import org.spongepowered.api.event.entity.DamageEntityEvent
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.plugin.PluginContainer
import team.ebi.aaa.api.Attribute
import team.ebi.aaa.api.data.{Mappings, TemplateSlot}
import team.ebi.aaa.data.DoubleRange
import team.ebi.aaa.data.DoubleRange.{Absolute, Relative}
import team.ebi.aaa.util.{listenTo, _}

import scala.annotation.tailrec
import scala.util.Random

trait AttackAttribute extends DoubleRangeAttribute {
  implicit def pluginContainer: PluginContainer

  @tailrec
  private def getRealEntity(target: AnyRef): Option[Player] = target match {
    case entity: Projectile => getRealEntity(entity.getShooter)
    case entity: Player => Some(entity)
    case _ => None
  }

  def getMappings(source: Player, target: Entity): Iterable[(TemplateSlot, Mappings)]

  listenTo[DamageEntityEvent] { event =>
    for (source <- event.getCause.first(classOf[EntityDamageSource]).asScala; entity <- getRealEntity(source.getSource)) {
      for ((_, mappings) <- getMappings(entity, event.getTargetEntity)) {
        val dataStream = Mappings.flattenDataStream(mappings, this)
        for (data <- dataStream.iterator.asScala) {
          event.setBaseDamage(data(Random)(event.getBaseDamage))
        }
      }
    }
  }
}
