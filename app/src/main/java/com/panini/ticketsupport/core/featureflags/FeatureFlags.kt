package com.panini.ticketsupport.core.featureflags

/**
 * Static feature flags controlling app behaviour.
 *
 * In a future phase these values will be fetched remotely via Firebase Remote Config,
 * allowing flags to be toggled per environment or user segment without a new release.
 * For now they are compile-time constants; change them here to test gated features locally.
 */
object FeatureFlags {
    const val CREATE_TICKET_ENABLED = true
    const val UPDATE_PRIORITY_ENABLED = true
    const val SHOW_CLOSED_TICKETS = false
    const val ADMIN_ACTIONS_ENABLED = false
}
