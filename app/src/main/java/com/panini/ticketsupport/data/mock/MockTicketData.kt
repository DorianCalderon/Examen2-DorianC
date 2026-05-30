package com.panini.ticketsupport.data.mock

import com.panini.ticketsupport.model.domain.Category
import com.panini.ticketsupport.model.domain.Priority
import com.panini.ticketsupport.model.domain.Ticket
import com.panini.ticketsupport.model.domain.TicketStatus
import java.time.LocalDateTime

val mockTickets: List<Ticket> = listOf(

    Ticket(
        id = "tk-001",
        title = "Supplier delay — Gold sticker series batch #7",
        description = "Panini's printing supplier in Italy has not shipped the Gold edition " +
                "sticker batch #7 for the FIFA WC 2026 album. Expected delivery was March 10; " +
                "no updated ETA received. Affects 12 regional distributors.",
        priority = Priority.CRITICAL,
        status = TicketStatus.OPEN,
        provider = "Panini S.p.A. — Italy",
        createdAt = LocalDateTime.of(2026, 3, 12, 8, 45),
        category = Category.SUPPLIER,
    ),

    Ticket(
        id = "tk-002",
        title = "Inventory shortage — Album covers Region Norte",
        description = "Warehouse in Monterrey reports only 200 album covers remaining out of " +
                "the 5,000 originally allocated. Restock request submitted but not yet approved. " +
                "Distributors in Nuevo León and Tamaulipas are affected.",
        priority = Priority.HIGH,
        status = TicketStatus.IN_PROGRESS,
        provider = "Distribuidora El Álamo S.A.",
        createdAt = LocalDateTime.of(2026, 3, 14, 10, 20),
        category = Category.INVENTORY,
    ),

    Ticket(
        id = "tk-003",
        title = "Wrong sticker packs shipped — Order #20260301",
        description = "Distributor received Group B sticker packs instead of the Group F packs " +
                "listed in order #20260301. Total mismatch: 3,400 packs. Return logistics " +
                "pending confirmation from the warehouse team.",
        priority = Priority.HIGH,
        status = TicketStatus.OPEN,
        provider = "Editorial Poch S.L.",
        createdAt = LocalDateTime.of(2026, 3, 15, 14, 5),
        category = Category.DISTRIBUTION,
    ),

    Ticket(
        id = "tk-004",
        title = "Missing shipment — Boxes 14–18 from batch #22B",
        description = "Five boxes (14 through 18) from shipment batch #22B never arrived at " +
                "the CDMX sorting hub. Tracking number shows 'In transit' since March 8. " +
                "Carrier has opened a lost-package investigation.",
        priority = Priority.CRITICAL,
        status = TicketStatus.IN_PROGRESS,
        provider = "Logística Veloz Express",
        createdAt = LocalDateTime.of(2026, 3, 8, 9, 0),
        category = Category.LOGISTICS,
    ),

    Ticket(
        id = "tk-005",
        title = "Damaged album covers — Batch 77B printing defect",
        description = "150 albums from batch 77B arrived with visible ink bleed on the front " +
                "cover, making the FIFA 2026 logo illegible. Quality control photos attached. " +
                "Replacement order has been requested from the printer.",
        priority = Priority.MEDIUM,
        status = TicketStatus.RESOLVED,
        provider = "Impresora Central S.A. de C.V.",
        createdAt = LocalDateTime.of(2026, 2, 28, 11, 30),
        category = Category.SUPPLIER,
    ),

    Ticket(
        id = "tk-006",
        title = "Customs clearance delay — Port of Veracruz",
        description = "A container with 80,000 sticker packs is held at Veracruz customs pending " +
                "a phytosanitary certificate for the adhesive materials. Legal team is preparing " +
                "the documentation; estimated release is 5 business days.",
        priority = Priority.HIGH,
        status = TicketStatus.IN_PROGRESS,
        provider = "Agencia Aduanal Golfo S.C.",
        createdAt = LocalDateTime.of(2026, 3, 17, 16, 50),
        category = Category.LOGISTICS,
    ),

    Ticket(
        id = "tk-007",
        title = "Duplicate invoices sent — Distributor Occidente",
        description = "The billing system issued two identical invoices (INV-2026-0892 and " +
                "INV-2026-0901) for the same March delivery to Distribuidora Occidente. " +
                "Finance has been notified; credit note pending.",
        priority = Priority.LOW,
        status = TicketStatus.CLOSED,
        provider = "Distribuidora Occidente Ltda.",
        createdAt = LocalDateTime.of(2026, 3, 5, 13, 15),
        category = Category.OTHER,
    ),

    Ticket(
        id = "tk-008",
        title = "Inventory count mismatch — Guadalajara warehouse",
        description = "Physical stock count at the Guadalajara warehouse shows 1,200 fewer " +
                "sticker packs than the system records. Discrepancy detected during the monthly " +
                "audit. Investigation ongoing to rule out theft or mis-scan.",
        priority = Priority.MEDIUM,
        status = TicketStatus.OPEN,
        provider = "Bodega GDL Norte",
        createdAt = LocalDateTime.of(2026, 3, 19, 7, 30),
        category = Category.INVENTORY,
    ),
)
