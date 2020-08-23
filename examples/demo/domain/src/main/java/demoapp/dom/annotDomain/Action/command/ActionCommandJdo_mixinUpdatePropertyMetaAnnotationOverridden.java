package demoapp.dom.annotDomain.Action.command;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;

//tag::class[]
@ActionCommandDisabledMetaAnnotation     // <.>
@Action(
    publishing = Publishing.ENABLED         // <.>
    , semantics = SemanticsOf.IDEMPOTENT
    , associateWith = "propertyMetaAnnotatedOverridden"
    , associateWithSequence = "2"
)
@ActionLayout(
    named = "Mixin Update Property"
    , describedAs =
        "@ActionPublishingDisabledMetaAnnotation " +
        "@Action(publishing = ENABLED)"
)
public class ActionCommandJdo_mixinUpdatePropertyMetaAnnotationOverridden {
    // ...
//end::class[]

    private final ActionCommandJdo actionCommandJdo;

    public ActionCommandJdo_mixinUpdatePropertyMetaAnnotationOverridden(ActionCommandJdo actionCommandJdo) {
        this.actionCommandJdo = actionCommandJdo;
    }

//tag::class[]
    public ActionCommandJdo act(final String value) {
        actionCommandJdo.setPropertyMetaAnnotatedOverridden(value);
        return actionCommandJdo;
    }
    public String default0Act() {
        return actionCommandJdo.getPropertyMetaAnnotatedOverridden();
    }
}
//end::class[]
