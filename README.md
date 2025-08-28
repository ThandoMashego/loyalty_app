Mukuru Loyalty Rewards Hub (Java)

A Java backend API for Mukuru’s loyalty rewards experience: **Send → Earn → Manage → Spend**.

## Features
- Simulated remittance transactions (input amount, validate, record)
- Points ledger (1 point per R100; floor rounding) + bonus hooks (e.g., first transfer)
- View points balance & transaction history
- Rewards marketplace (list, details) & redemption (atomic stock + points deduction)
- Optional: tiers (Bronze/Silver/Gold), badges, leaderboard
- Clean architecture (controllers → services → repositories → entities)

## Tech Stack


## Quick Start
```bash

```
API base: `http://localhost:8080`

## API (excerpt)
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/remittances` {amount}
- `GET /api/points/balance`
- `GET /api/points/ledger?offset=&limit=`
- `GET /api/rewards`
- `GET /api/rewards/{id}`
- `POST /api/redemptions` {rewardId}
- `GET /api/tiers/me`
- `GET /api/leaderboard?limit=20`
- `POST /api/admin/rewards`
- `PATCH /api/admin/rewards/{id}`

## Domain Model (Entities)
- User(id, email, passwordHash, displayName, tier, createdAt)
- RemittanceTransaction(id, userId, amount, createdAt, status, reference, channel)
- PointLedgerEntry(id, userId, sourceType[REMITTANCE|BONUS|ADJUSTMENT|REDEMPTION], sourceId, pointsDelta, balanceAfter, createdAt)
- Reward(id, name, description, pointsCost, category, stock, imageUrl, isActive)
- Redemption(id, userId, rewardId, pointsSpent, status[PENDING|FULFILLED|CANCELLED], code, createdAt, fulfilledAt)
- Tier(code[BRONZE|SILVER|GOLD], threshold)
- Badge(id, code, name, description, icon)
- UserBadge(id, userId, badgeId, awardedAt)
- LeaderboardEntry(id, userId, lifetimePoints, rank, updatedAt)

## Points Logic
- **Baseline**: `points = floor(amount / 100)`
- **Bonuses**: First Transfer +10, High-Value Send (>= R2000) +20 (example rules)

## Redemption Transaction (Atomic)
1) Check user balance ≥ reward.pointsCost
2) Lock or decrement reward stock
3) Deduct points (write ledger with negative delta)
4) Create redemption with code
5) Commit

## Package Structure (suggested)
```
com.mukuru.loyalty
  ├─ api        (controllers, DTOs, exception handlers)
  ├─ domain     (entities, enums)
  ├─ service    (interfaces + impl)
  ├─ repo       (Spring Data repositories)
  ├─ config     (security, db, object mapping)
  ├─ util       (calculators, mappers)
  └─ test       (BDD tests, service tests)
```

## Testing
- Unit tests for calculators (points, tiers)
- Service tests for redemption transactionality
- BDD-style acceptance tests for user stories (Given/When/Then)

## Roadmap
- Carts & multi-item redemption
- Friend leaderboards
- Streak badges & seasonal campaigns
- Admin dashboards & analytics

---
*Hackathon focus: deliver an end-to-end demo with smooth UX and clear separation of concerns.*
