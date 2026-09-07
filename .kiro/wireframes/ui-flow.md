# SkillSeed — Wireframe UI Flow

> **Tài liệu wireframe chi tiết cho toàn bộ user flow của SkillSeed.**
> **Format:** ASCII wireframes (text-based, dễ copy vào Figma/whimsical).
> **Phạm vi:** 30+ màn hình chính, từ marketing landing → admin dashboard.
> **Nguyên tắc:** Mobile-first, sau đó desktop. Mỗi screen có mục đích + annotations rõ ràng.

---

## Mục lục

### Part A — Public & Auth
1. [Landing Page (Marketing)](#1-landing-page)
2. [Sign Up](#2-sign-up)
3. [Login](#3-login)
4. [Verify Email](#4-verify-email)
5. [Forgot Password](#5-forgot-password)
6. [Reset Password](#6-reset-password)

### Part B — Onboarding
7. [Onboarding — Welcome](#7-onboarding-welcome)
8. [Onboarding — Skills I Can Teach](#8-onboarding-step-1-skills-i-can-teach)
9. [Onboarding — Skills I Want to Learn](#9-onboarding-step-2-skills-i-want-to-learn)
10. [Onboarding — Goals & Style](#10-onboarding-step-3-goals--style)
11. [Onboarding — Availability](#11-onboarding-step-4-availability)
12. [Onboarding — Languages & Country](#12-onboarding-step-5-languages--country)
13. [Onboarding — Avatar & Bio](#13-onboarding-step-6-avatar--bio)
14. [Onboarding — Done](#14-onboarding-done)

### Part C — Main App
15. [Home / Discover](#15-home--discover)
16. [Discover Filters](#16-discover-filters)
17. [User Profile (Public)](#17-user-profile-public)
18. [Booking Modal](#18-booking-modal)
19. [Bookings List](#19-bookings-list)
20. [Booking Detail](#20-booking-detail)
21. [Video Session](#21-video-session)
22. [Rating Modal](#22-rating-modal)
23. [My Wallet](#23-my-wallet)
24. [My Profile (Edit)](#24-my-profile-edit)
25. [Notifications](#25-notifications)

### Part D — Admin & B2B
26. [Admin Dashboard](#26-admin-dashboard)
27. [B2B Org Dashboard](#27-b2b-org-dashboard)

---

## Conventions trong wireframe

- `[ ]` = Button
- `[input]` = Input field
- `( )` = Radio / Checkbox
- `[▼]` = Dropdown
- `───` = Divider
- `→` = Flow direction
- `{ }` = Conditional / dynamic content
- `⚠️` = Required field
- `ℹ️` = Tooltip
- `[Icon]` = Icon button

**Color codes (gợi ý):**
- Primary CTA: `#10B981` (Emerald-500, xanh SkillSeed)
- Secondary: `#6366F1` (Indigo-500)
- Warning: `#F59E0B`
- Danger: `#EF4444`
- Text: `#111827`
- Muted: `#6B7280`
- Background: `#F9FAFB`

---

# PART A — PUBLIC & AUTH

## 1. Landing Page

**Mục đích:** Convert visitor → email signup. Đo CTR trên hero CTA.

**Routes:** `/` (skillseed.app)
**Devices:** Mobile (priority), Desktop
**Conversion goal:** Email signup (≥ 8% visitor → email)

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                       [Login] [Get Started]  │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│         Teach what you know.                                 │
│         Learn what you love.                                 │
│         Pay with your time, not your wallet.                 │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ your@email.com          │                    │
│              └─────────────────────────┘                    │
│              ┌─────────────────────────┐                    │
│              │ Get Early Access    →   │                    │
│              └─────────────────────────┘                    │
│                                                              │
│         🌱 30 free starter seeds when you join               │
│                                                              │
│       [Hero image: 2 người video call, mini screen share]   │
│                                                              │
└─────────────────────────────────────────────────────────────┘

  ─── scroll down ───

┌─────────────────────────────────────────────────────────────┐
│  THE PROBLEM                                                 │
│                                                              │
│  ❌ Online courses are expensive ($200-$2,000)              │
│  ❌ Hard to find a mentor who actually teaches you          │
│  ❌ Lonely online learning — no real connection             │
│                                                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  THE SOLUTION: 3 pillars                                     │
│                                                              │
│   💚 Money-Less        🤖 AI-Matched       ✓ Verified       │
│   Trao đổi bằng        Ghép cặp dựa        Skill Passport   │
│   thời gian            trên Skill DNA       trên blockchain  │
│                                                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  HOW IT WORKS (3 steps)                                      │
│                                                              │
│   ①  Tạo Skill DNA          ②  AI matching         ③ Book  │
│   ┌──────────┐              ┌──────────┐         ┌────────┐│
│   │ 3 phút   │              │ Top 10   │         │Video   ││
│   │ form     │      →       │ matches  │   →     │call 1-1││
│   └──────────┘              └──────────┘         └────────┘│
│                                                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  BENEFITS (grid 2 cols)                                      │
│                                                              │
│  ┌─FOR LEARNERS─────────┐  ┌─FOR TEACHERS─────────────┐    │
│  │ • Học miễn phí       │  │ • Dạy khi rảnh           │    │
│  │ • 1-1, cá nhân hoá   │  │ • Nhận Seeds              │    │
│  │ • Chọn mentor global  │  │ • Xây network            │    │
│  │ • Skill Passport      │  │ • Skill Passport         │    │
│  └──────────────────────┘  └──────────────────────────┘    │
│                                                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  SOCIAL PROOF (placeholder)                                  │
│                                                              │
│  ⭐⭐⭐⭐⭐  "SkillSeed helped me land my first dev job"  │
│  — Mai Tran, Junior Developer                               │
│                                                              │
│  (Sẽ có khi có beta users)                                  │
│                                                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  FAQ                                                         │
│                                                              │
│  ▸ Is SkillSeed really free?                                │
│  ▸ How do Skill Seeds work?                                 │
│  ▸ What if I don't have skills to teach?                    │
│  ▸ Is it safe?                                              │
│  ▸ Which countries are supported?                           │
│  ▸ Can I become a Premium mentor?                           │
│                                                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  FINAL CTA                                                   │
│                                                              │
│   Ready to teach what you know?                              │
│                                                              │
│   ┌──────────────┐  ┌─────────────────────────┐           │
│   │ email        │  │ Join Waitlist  →        │           │
│   └──────────────┘  └─────────────────────────┘           │
│                                                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  Footer                                                      │
│  Privacy  |  Terms  |  Contact  |  Twitter  |  LinkedIn    │
│  © 2026 SkillSeed                                            │
└─────────────────────────────────────────────────────────────┘
```

**Annotations:**
- CTA copy variations để A/B test: "Get Early Access" / "Join Waitlist" / "Start Free".
- Tracking: GA4 event `hero_cta_click`, `scroll_depth`, `email_signup`.
- Mobile breakpoint: stack hero image dưới CTA.
- Cookie banner: hiển thị 1 lần, có Accept/Decline.

---

## 2. Sign Up

**Mục đích:** Tạo account. Email → OTP → vào onboarding.

**Routes:** `/register`
**Conversion goal:** Submit form ≥ 60%

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                              [Already member?]│
│                                              → Login         │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│              Create your account                             │
│              ──────────────────                              │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Full name ⚠️            │                    │
│              └─────────────────────────┘                    │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Email ⚠️                │                    │
│              └─────────────────────────┘                    │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Password ⚠️    [👁]      │                    │
│              │ ≥ 8 chars + chữ + số    │                    │
│              └─────────────────────────┘                    │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Confirm password ⚠️     │                    │
│              └─────────────────────────┘                    │
│                                                              │
│              ( ) I agree to Terms & Privacy ⚠️              │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Create Account →        │                    │
│              └─────────────────────────┘                    │
│                                                              │
│              ─── or sign up with ───                         │
│                                                              │
│              [G  Continue with Google]                       │
│              [🍎 Continue with Apple]                        │
│                                                              │
│              Already have an account? [Login]                │
│                                                              │
└─────────────────────────────────────────────────────────────┘

  ─── on submit success ───

┌─────────────────────────────────────────────────────────────┐
│                                                              │
│              📧 Check your email!                            │
│                                                              │
│      We've sent a confirmation link to:                     │
│                                                              │
│              user@example.com                                │
│                                                              │
│      Click the link to activate your account.               │
│      (Link expires in 24 hours)                             │
│                                                              │
│              [Resend email]                                  │
│              [Use different email]                           │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

**Validation rules:**
- Email: RFC 5322 + check uniqueness sau submit.
- Password: ≥ 8 chars, có ít nhất 1 chữ + 1 số.
- Tất cả fields realtime validate (on blur), nút submit enable khi hợp lệ.
- Hiển thị password strength meter (optional).

**Tracking:** GA4 `signup_started`, `signup_completed`, `signup_failed_reason`.

---

## 3. Login

**Mục đích:** Authenticate user hiện tại.

**Routes:** `/login`

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                                               │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│              Welcome back                                    │
│              ────────────                                    │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Email ⚠️                │                    │
│              └─────────────────────────┘                    │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Password ⚠️    [👁]      │                    │
│              └─────────────────────────┘                    │
│                                                              │
│                                       [Forgot password?]     │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Login →                 │                    │
│              └─────────────────────────┘                    │
│                                                              │
│              ─── or ───                                      │
│                                                              │
│              [G  Continue with Google]                       │
│              [🍎 Continue with Apple]                        │
│                                                              │
│              Don't have an account? [Sign up]               │
│                                                              │
└─────────────────────────────────────────────────────────────┘

  ─── on 5 failed attempts ───

  ⚠️ Too many attempts. Try again in 15 minutes.
     [Forgot password → reset via email]
```

**Rate limiting:** 5 attempts/IP/15 min → block.

---

## 4. Verify Email

**Mục đích:** Active account từ link email.

**Routes:** `/verify-email/[token]`

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                                               │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│                  (Loading spinner)                           │
│                                                              │
│              Verifying your email...                         │
│                                                              │
└─────────────────────────────────────────────────────────────┘

  ─── on success ───

┌─────────────────────────────────────────────────────────────┐
│                                                              │
│                  ✅ Email verified!                          │
│                                                              │
│              Welcome to SkillSeed, Mai!                      │
│                                                              │
│              Bạn đã nhận 30 free starter seeds. 🌱          │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Complete your profile → │                    │
│              └─────────────────────────┘                    │
│                                                              │
└─────────────────────────────────────────────────────────────┘

  ─── on expired token ───

┌─────────────────────────────────────────────────────────────┐
│                                                              │
│                  ⚠️ Link expired                            │
│                                                              │
│      This verification link has expired.                     │
│                                                              │
│              [Resend verification email]                     │
│              [Back to login]                                 │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 5. Forgot Password

**Mục đích:** Request reset link.

**Routes:** `/forgot-password`

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                            [← Back to login]  │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│              Reset your password                             │
│              ──────────────────                              │
│                                                              │
│      Enter the email address associated with your account.   │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Email ⚠️                │                    │
│              └─────────────────────────┘                    │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Send reset link →       │                    │
│              └─────────────────────────┘                    │
│                                                              │
└─────────────────────────────────────────────────────────────┘

  ─── on submit success (always show same screen) ───

┌─────────────────────────────────────────────────────────────┐
│                                                              │
│              📧 Check your email                             │
│                                                              │
│      If an account exists for that email,                   │
│      we've sent a password reset link.                      │
│      (Check spam folder too)                                │
│                                                              │
│              [← Back to login]                               │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

**Security note:** Luôn show success message (không tiết lộ email có tồn tại hay không).

---

## 6. Reset Password

**Mục đích:** Đặt password mới.

**Routes:** `/reset-password/[token]`

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                                               │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│              Set new password                                │
│              ────────────────                                │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ New password ⚠️  [👁]    │                    │
│              │ ≥ 8 chars + chữ + số    │                    │
│              └─────────────────────────┘                    │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Confirm password ⚠️     │                    │
│              └─────────────────────────┘                    │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Reset password →        │                    │
│              └─────────────────────────┘                    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

# PART B — ONBOARDING (7 steps)

> **Mục đích chung:** Thu thập Skill DNA trong ~3 phút. Progress bar luôn hiển thị.
> **Auto-save:** Mỗi step tự lưu khi user next. Cho phép back & resume.
> **Exit:** Có nút "Save & continue later" ở mỗi step.

## 7. Onboarding — Welcome

**Routes:** `/onboarding`

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                              [Save & exit]    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   ● ● ● ● ● ● ●    Step 0/7                                │
│                                                              │
│         ────────────────────────────                        │
│                                                              │
│         Welcome, Mai! 👋                                     │
│                                                              │
│         Let's build your Skill DNA —                        │
│         a 3-minute profile that helps AI find               │
│         perfect matches for you.                            │
│                                                              │
│         🎁 You'll get 30 free starter seeds after.          │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Let's go →              │                    │
│              └─────────────────────────┘                    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 8. Onboarding — Step 1: Skills I Can Teach

**Mục đích:** Thu thập offered skills.

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                              [Save & exit]    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   ● ● ● ● ● ● ●    Step 1/7                                │
│                                                              │
│   What can you teach?                                       │
│   ────────────────────────                                  │
│                                                              │
│   Search skills or add your own                             │
│   ┌───────────────────────────────┐                         │
│   │ 🔍 Type a skill...            │                         │
│   └───────────────────────────────┘                         │
│                                                              │
│   Suggestions based on your profile:                         │
│   ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐              │
│   │+ Public│ │+ Python│ │+ Yoga  │ │+ Cook- │              │
│   │ Speaking   │        │        │  ing    │              │
│   └────────┘ └────────┘ └────────┘ └────────┘              │
│                                                              │
│   ───────────────────────────────────────                   │
│   Your teaching skills (3):                                  │
│                                                              │
│   ┌────────────────────────────────────┐                   │
│   │ 📊 Public Speaking          [×]    │                   │
│   │    Level: ●●●○○ (3/5)              │                   │
│   │    Years: [5]   Rate: [60] seeds/h │                   │
│   │    [Edit description...]            │                   │
│   ├────────────────────────────────────┤                   │
│   │ 💻 Python Programming       [×]    │                   │
│   │    Level: ●●●●○ (4/5)              │                   │
│   │    Years: [3]   Rate: [60] seeds/h │                   │
│   └────────────────────────────────────┘                   │
│                                                              │
│   [+ Add custom skill]                                       │
│                                                              │
│              [← Back]      [Next →]                         │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

**UI patterns:**
- Skill chip với level dots (1–5).
- Tap chip "Edit description" → modal với textarea (500 chars max).
- Custom skill: text input → auto-suggest taxonomy → confirm "Add as new".
- Min 1 skill required để next.

---

## 9. Onboarding — Step 2: Skills I Want to Learn

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                              [Save & exit]    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   ● ● ● ● ● ● ●    Step 2/7                                │
│                                                              │
│   What do you want to learn?                                │
│   ──────────────────────────────                            │
│                                                              │
│   ┌───────────────────────────────┐                         │
│   │ 🔍 Search skills...           │                         │
│   └───────────────────────────────┘                         │
│                                                              │
│   ─── Suggested based on your profile ───                   │
│                                                              │
│   ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐              │
│   │+ System│ │+ UX Re-│ │+ Busi- │ │+ Data  │              │
│   │ Design │ │ search │ │ ness   │ │ Science│              │
│   └────────┘ └────────┘ └────────┘ └────────┘              │
│                                                              │
│   ─── Your learning goals (4) ───                           │
│                                                              │
│   ┌────────────────────────────────────┐                   │
│   │ 🎨 System Design           [×]    │                   │
│   │    Priority: ●●●●○ (4/5)           │                   │
│   │    Target level: [3] (1-5)         │                   │
│   ├────────────────────────────────────┤                   │
│   │ 🇬🇧 Business English       [×]    │                   │
│   │    Priority: ●●●○○ (3/5)           │                   │
│   └────────────────────────────────────┘                   │
│                                                              │
│              [← Back]      [Next →]                         │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 10. Onboarding — Step 3: Goals & Style

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                              [Save & exit]    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   ● ● ● ● ● ● ●    Step 3/7                                │
│                                                              │
│   Your goals & learning style                               │
│   ────────────────────────────                              │
│                                                              │
│   What's your main goal?                                    │
│                                                              │
│   ( ) 🎯 Career advancement                                │
│   ( ) 🌱 Personal hobby                                     │
│   ( ) 🔄 Career switch                                      │
│   ( ) 💼 Build a business                                   │
│   ( ) 🧠 Just curious                                       │
│                                                              │
│   How do you learn best?                                    │
│                                                              │
│   ( ) 👀 Visual (diagrams, videos)                          │
│   ( ) 👂 Auditory (discussions, podcasts)                   │
│   ( ) 📖 Reading (text, articles)                           │
│   ( ) ✋ Kinesthetic (practice, hands-on)                    │
│                                                              │
│   Bio (optional)                                             │
│   ┌────────────────────────────────────┐                   │
│   │ Tell others about yourself...      │                   │
│   │                                    │                   │
│   │                        0/500 chars │                   │
│   └────────────────────────────────────┘                   │
│                                                              │
│              [← Back]      [Next →]                         │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 11. Onboarding — Step 4: Availability

**Mục đích:** Set weekly recurring free slots.

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                              [Save & exit]    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   ● ● ● ● ● ● ●    Step 4/7                                │
│                                                              │
│   When are you usually free?                                │
│   ────────────────────────────                              │
│                                                              │
│   Timezone: [Asia/Ho_Chi_Minh ▼]                            │
│                                                              │
│   Tap slots when you're available each week.                 │
│                                                              │
│         Mon  Tue  Wed  Thu  Fri  Sat  Sun                   │
│   06:00  [ ]  [ ]  [ ]  [ ]  [ ]  [ ]  [ ]                │
│   07:00  [ ]  [ ]  [ ]  [ ]  [ ]  [ ]  [ ]                │
│   08:00  [✓]  [✓]  [✓]  [✓]  [✓]  [ ]  [ ]                │
│   09:00  [✓]  [✓]  [✓]  [✓]  [✓]  [ ]  [ ]                │
│   ...                                                        │
│   18:00  [✓]  [✓]  [ ]  [✓]  [ ]  [ ]  [ ]                │
│   19:00  [✓]  [✓]  [ ]  [✓]  [ ]  [ ]  [ ]                │
│   20:00  [✓]  [✓]  [ ]  [✓]  [ ]  [ ]  [ ]                │
│   21:00  [✓]  [✓]  [ ]  [✓]  [ ]  [ ]  [ ]                │
│                                                              │
│   Total: 18 hours/week                                      │
│                                                              │
│   💡 Tip: Set at least 2-3 hours/week to get matches.       │
│                                                              │
│              [← Back]      [Next →]                         │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

**UI patterns:**
- Grid 7 cols × N rows (1h slots).
- Tap để toggle. Drag để multi-select.
- Mobile: scroll ngang + pinch zoom.
- Show timezone hint cho user khác.

---

## 12. Onboarding — Step 5: Languages & Country

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                              [Save & exit]    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   ● ● ● ● ● ● ●    Step 5/7                                │
│                                                              │
│   Languages & location                                      │
│   ────────────────────────                                  │
│                                                              │
│   Languages you speak (multi-select)                        │
│                                                              │
│   [✓] 🇻🇳 Vietnamese      [✓] 🇺🇸 English                  │
│   [ ] 🇯🇵 Japanese         [ ] 🇰🇷 Korean                  │
│   [ ] 🇨🇳 Chinese          [ ] 🇫🇷 French                  │
│   [+] Add language                                           │
│                                                              │
│   ─────────────────────────────────────                     │
│                                                              │
│   Country                                                    │
│                                                              │
│   [🇻🇳 Vietnam                       ▼]                      │
│                                                              │
│              [← Back]      [Next →]                         │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 13. Onboarding — Step 6: Avatar & Photo

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                              [Save & exit]    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   ● ● ● ● ● ● ●    Step 6/7                                │
│                                                              │
│   Add a profile photo                                       │
│   ────────────────────                                      │
│                                                              │
│   ┌──────────┐                                              │
│   │          │   Why add a photo?                            │
│   │   👤    │   • Profiles with photos get 3× more matches  │
│   │          │   • Helps people recognize you in sessions    │
│   └──────────┘                                              │
│   [Upload photo]                                             │
│                                                              │
│   ─────────────────────────────────────                     │
│                                                              │
│   Or choose an avatar:                                      │
│   ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐                          │
│   │🦊│ │🐼│ │🐱│ │🐯│ │🦁│ │🐰│                          │
│   └──┘ └──┘ └──┘ └──┘ └──┘ └──┘                          │
│                                                              │
│              [← Back]      [Next →]                         │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

**Validation:** Ảnh ≤ 5MB, image/* MIME. Auto-crop thành 1:1.

---

## 14. Onboarding — Done

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                                               │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   ● ● ● ● ● ● ●    Done! 🎉                                 │
│                                                              │
│         ┌────────────────────────┐                         │
│         │     ✨    ✨    ✨      │                         │
│         │                        │                         │
│         │  Your Skill DNA is     │                         │
│         │       complete!        │                         │
│         │                        │                         │
│         └────────────────────────┘                         │
│                                                              │
│   Here's your profile summary:                               │
│                                                              │
│   📊 Public Speaking (Lvl 3) — 5 years                      │
│   💻 Python (Lvl 4) — 3 years                               │
│   🎨 Want to learn: System Design, UX                       │
│   🕐 18 hours/week available                                │
│   🌐 VN, EN                                                  │
│                                                              │
│   🌱 30 starter seeds added to your wallet!                 │
│                                                              │
│   → AI is finding your top 10 matches...                    │
│                                                              │
│              ┌─────────────────────────┐                    │
│              │ Show me my matches →    │                    │
│              └─────────────────────────┘                    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

**Trigger:** Click CTA → fire `user.onboarding_completed` event → redirect `/discover`.

---

---

# PART C — MAIN APP

## 15. Home / Discover

**Mục đích:** Daily AI-matched list. Entry point sau onboarding.
**Routes:** `/discover`
**Performance:** First contentful paint < 1.5s. Skeleton loader trong khi chờ AI.

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                                               │
│  ─────────────────────────────────────────────────          │
│  🏠 Discover  📅 Bookings  💰 Wallet  👤 Profile           │
├─────────────────────────────────────────────────────────────┤
│  🔍 [Search skills or people...                ] [Filter ⚙] │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ✨ Top matches for you today                                │
│  ──────────────────────────────                             │
│  Why these? AI picked based on your skill DNA + schedule.   │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ [Avatar]  Mai Tran          ⭐ 4.9 (23 sessions)    │   │
│  │           🌳 Tree tier  ✓ Verified  🇻🇳 Vietnam      │   │
│  │           ────────────────────────────────────────   │   │
│  │           💬 "Teaches Public Speaking, Storytelling"│   │
│  │           🕐 Free today 6-9 PM                      │   │
│  │                                                     │   │
│  │           ℹ️ Why we matched:                        │   │
│  │           "92% match — Both available evenings,    │   │
│  │            she teaches Public Speaking at level 5"  │   │
│  │                                                     │   │
│  │           [Book session →]    [View profile]        │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ [Avatar]  John Smith         ⭐ 4.8 (47 sessions)    │   │
│  │           🌲 Forest tier  ✓ Verified  🇸🇬 Singapore  │   │
│  │           ────────────────────────────────────────   │   │
│  │           💻 "Teaches Python, System Design"        │   │
│  │           🕐 Free tomorrow 7-10 AM                  │   │
│  │                                                     │   │
│  │           [Book session →]    [View profile]        │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                              │
│           [Show 8 more matches]                              │
│                                                              │
│  ────────────────────────────────────────                   │
│  🎓 Top mentors this week                                    │
│                                                              │
│  [chip: Public Speaking] [chip: Python] [chip: UX Design]   │
│  [chip: Yoga]              [chip: Photography]               │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

**State variations:**
- Loading: skeleton cards
- Empty: "No matches yet — try widening filters"
- New user (cold start): show "Founding Mentor" badge
- Premium users: AI insights badge per match

---

## 16. Discover Filters

**Mục đích:** Narrow down AI matches.

```
┌─────────────────────────────────────────────────────────────┐
│  ← Filter matches                                      [×] │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Skill                                                       │
│  ┌───────────────────────────────┐                          │
│  │ Any skill                  ▼  │                          │
│  └───────────────────────────────┘                          │
│                                                              │
│  Language                                                    │
│  [✓] Vietnamese  [✓] English  [ ] Japanese  [+] Add        │
│                                                              │
│  Country                                                     │
│  [✓] Vietnam  [ ] Singapore  [ ] Global  [+] Add           │
│                                                              │
│  Minimum rating                                              │
│  ────●──────  3.5+                                            │
│                                                              │
│  Tier                                                        │
│  [✓] Any  [ ] Sprout  [✓] Sapling  [✓] Tree+               │
│                                                              │
│  Availability                                                │
│  [✓] Available this week                                     │
│  [ ] Available today                                         │
│  [ ] Available weekends                                      │
│                                                              │
│  Verified only?                                              │
│  [✓] Yes (verified identity + phone)                         │
│                                                              │
│  ──────────────────────────────────────                     │
│                                                              │
│  [Reset]                       [Apply filters →]             │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 17. User Profile (Public)

**Mục đích:** Xem chi tiết người dùng khác → quyết định book session.

```
┌─────────────────────────────────────────────────────────────┐
│  ← Back                                                     │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌────────┐   Mai Tran                                        │
│  │        │   🌳 Tree  ✓ Verified  🇻🇳 Vietnam                │
│  │ [👤]  │   ⭐ 4.9 (23 sessions)                             │
│  │        │   💚 145 sessions taught                          │
│  └────────┘                                                  │
│                                                              │
│  "Product designer passionate about helping others find     │
│   their voice. 8 years experience in public speaking."       │
│                                                              │
│  ── Skills I teach ──                                       │
│  • Public Speaking (Lvl 5, 8y, 60 seeds/h)                 │
│  • Storytelling (Lvl 4, 6y, 60 seeds/h)                     │
│  • Presentation Design (Lvl 4, 5y, 80 seeds/h)              │
│                                                              │
│  ── Languages ──                                            │
│  Vietnamese (native), English (fluent)                       │
│                                                              │
│  ── Availability (next 7 days) ──                            │
│  ┌─────────────────────────────────────────┐                │
│  │ Mon  ●●●○○  18:00-21:00                 │                │
│  │ Tue  ●●●○○  18:00-21:00                 │                │
│  │ Wed  ●●○○○  19:00-20:30                 │                │
│  │ Thu  ●●●○○  18:00-21:00                 │                │
│  │ Fri  ✗       -                          │                │
│  │ Sat  ✗       -                          │                │
│  │ Sun  ●●●○○  14:00-18:00                 │                │
│  └─────────────────────────────────────────┘                │
│  (Timezone: GMT+7 — your time)                              │
│                                                              │
│  ── Recent reviews ──                                        │
│  ⭐⭐⭐⭐⭐ "Amazing mentor! Helped me land a presentation    │
│             at Google." — Linh N., 2 weeks ago              │
│                                                              │
│  ⭐⭐⭐⭐⭐ "Patient and clear. Highly recommend."            │
│             — Duc M., 1 month ago                            │
│                                                              │
│  ── Skill Passport ──                                       │
│  ✓ Public Speaking  [View passport →]                       │
│                                                              │
│  ══════════════════════════════════════════                  │
│                                                              │
│  ┌─────────────────────────────────────┐                   │
│  │  Book a session →                   │                   │
│  │  (60 seeds = 1 hour)                │                   │
│  └─────────────────────────────────────┘                   │
│                                                              │
│  [💬 Message] [🚩 Report]                                   │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 18. Booking Modal

**Mục đích:** Confirm trước khi tạo booking.

```
┌─────────────────────────────────────────────────────────────┐
│  ✕ Book a session with Mai Tran                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Skill                                                       │
│  [Public Speaking                              ▼]            │
│                                                              │
│  Date                                                        │
│  ┌───────────────────────────────┐                          │
│  │ 📅 Thursday, Sep 11          │                          │
│  └───────────────────────────────┘                          │
│                                                              │
│  Time slot                                                   │
│  ┌─────────────────────────────────────┐                   │
│  │ ● 18:00 - 18:30  (30 min, 30 sd)  │                   │
│  │ ○ 18:30 - 19:00  (30 min, 30 sd)  │                   │
│  │ ○ 19:00 - 20:00  (60 min, 60 sd)  │                   │
│  │ ○ 20:00 - 21:00  (60 min, 60 sd)  │                   │
│  └─────────────────────────────────────┘                   │
│                                                              │
│  Duration                                                    │
│  ( ) 15 min  ( ) 30 min  (●) 60 min                         │
│                                                              │
│  Message to mentor (optional)                                │
│  ┌─────────────────────────────────────┐                   │
│  │ Hi Mai, I'd love to learn...        │                   │
│  │                                     │                   │
│  └─────────────────────────────────────┘                   │
│                                                              │
│  ──────────────────────────────────────                     │
│                                                              │
│  Cost: 60 seeds                                              │
│  Your balance: 75 seeds ✓                                    │
│                                                              │
│  ⓘ Cancellation policy:                                      │
│    • Cancel ≥ 24h before: 100% refund                       │
│    • Cancel < 24h before: 50% refund                        │
│    • No-show: 0% refund                                     │
│                                                              │
│  ┌─────────────────────────────────────┐                   │
│  │  Confirm booking (60 seeds)  →      │                   │
│  └─────────────────────────────────────┘                   │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 19. Bookings List

**Mục đích:** Quản lý sessions sắp tới + lịch sử.

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                                               │
│  ─────────────────────────────────────────────────          │
│  🏠 Discover  📅 Bookings●  💰 Wallet  👤 Profile          │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  [Upcoming] [Past] [Cancelled]                               │
│                                                              │
│  ── Today ──                                                │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ ⏰ Starts in 2 hours                                │   │
│  │ [Avatar] Mai Tran — Public Speaking                  │   │
│  │          📅 Today, 19:00-20:00 (60 min)              │   │
│  │          Status: ✅ Confirmed                        │   │
│  │                                                     │   │
│  │          [Join session →]   [Reschedule] [Cancel]   │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                              │
│  ── Tomorrow ──                                             │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ [Avatar] John Smith — Python                        │   │
│  │          📅 Tomorrow, 10:00-10:30 (30 min)           │   │
│  │          Status: ⏳ Pending (awaiting acceptance)    │   │
│  │                                                     │   │
│  │          [Cancel request]                            │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                              │
│  ── This week ──                                            │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ [Avatar] An Nguyen — UX Research                    │   │
│  │          📅 Saturday, 14:00-15:00 (60 min)           │   │
│  │          Status: ✅ Confirmed                        │   │
│  │                                                     │   │
│  │          [Join session →]   [Cancel]                 │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 20. Booking Detail

**Mục đích:** Xem full info, join session, cancel, rate.

```
┌─────────────────────────────────────────────────────────────┐
│  ← Bookings                                                 │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌────────┐   Session with Mai Tran                          │
│  │  [👤] │   📅 Thursday, Sep 11, 19:00-20:00               │
│  └────────┘   (GMT+7 — 60 minutes)                          │
│                                                              │
│  Status:  ✅ Confirmed (accepted 2 days ago)                │
│                                                              │
│  ════════════════════════════════════════════════            │
│                                                              │
│  Countdown                                                   │
│  ┌─────────────────────────────────────┐                   │
│  │      ⏰  01:42:35  until session    │                   │
│  └─────────────────────────────────────┘                   │
│                                                              │
│  ════════════════════════════════════════════════            │
│                                                              │
│  Skill: Public Speaking                                      │
│  Cost: 60 seeds (escrow held)                                │
│                                                              │
│  ── Meeting ──                                              │
│  Link will be available 10 min before start.                 │
│  [📋 Copy meeting link]   [Add to Google Calendar]          │
│                                                              │
│  ── Your message ──                                          │
│  "Hi Mai, I'd love to learn about..."                       │
│                                                              │
│  ── Actions ──                                                │
│  ┌─────────────────────────────────────┐                   │
│  │       Join session (off until 18:50)│                   │
│  └─────────────────────────────────────┘                   │
│                                                              │
│  [Cancel booking]                                            │
│                                                              │
│  ════════════════════════════════════════════════            │
│                                                              │
│  After session: rate your experience                         │
│  (form appears when status = completed)                      │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

**State variations theo status:**
- `pending`: "Waiting for Mai to accept (auto-decline in 24h)"
- `confirmed`: countdown + join button
- `in_progress`: green border, "Session live" indicator
- `completed`: show rating form
- `cancelled`: muted, "Refunded 60 seeds"
- `no_show`: red border, "Marked no-show"

---

## 21. Video Session

**Mục đích:** Trải nghiệm học 1-1 qua video call.

```
┌─────────────────────────────────────────────────────────────┐
│  ┌─────────────────────────────────┐  ┌────────────┐       │
│  │                                 │  │            │       │
│  │                                 │  │  You       │       │
│  │      Mai Tran (Teacher)         │  │  (PiP)     │       │
│  │                                 │  │            │       │
│  │                                 │  └────────────┘       │
│  │  🟢 Connected  • 18:42/60:00   │                        │
│  │                                 │                        │
│  └─────────────────────────────────┘                        │
│                                                              │
│  [Notes tab]  [Chat tab]  [Resources tab]                    │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ 09:42 — Discussed opening hook techniques            │  │
│  │ 09:51 — Action item: practice 3-min pitch by Friday  │  │
│  │ 10:05 — Aha moment: "feel the pause, don't fill it"  │  │
│  │                                                       │  │
│  │ ✨ AI Co-Pilot is taking notes                       │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
├─────────────────────────────────────────────────────────────┤
│  [🎤]  [📷]  [🖥️ Share]  [🎨 Whiteboard]  [💬 Chat]        │
│                                                              │
│  [⚙ Settings]                          [✕ Leave session]   │
└─────────────────────────────────────────────────────────────┘
```

**Controls bar (bottom):**
- Mic on/off
- Camera on/off
- Share screen (Daily.co API)
- Whiteboard toggle
- Chat panel toggle
- Settings (bandwidth, audio device)
- Leave (with confirm modal)

**Side panels:**
- Chat (text)
- Resources (teacher uploads files)
- Notes (AI Co-Pilot auto-notes — Phase 2)
- Participants (Pod sessions)

---

## 22. Rating Modal

**Mục đích:** Thu thập feedback sau session.

```
┌─────────────────────────────────────────────────────────────┐
│  ✕ How was your session?                                    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Session with Mai Tran — Public Speaking                     │
│  📅 Sep 11, 19:00-20:00                                     │
│                                                              │
│  Rate 5 dimensions (each 1–5 stars):                        │
│                                                              │
│  Knowledge of subject                                       │
│  ☆ ☆ ☆ ☆ ☆                                                  │
│                                                              │
│  Clarity of explanation                                     │
│  ☆ ☆ ☆ ☆ ☆                                                  │
│                                                              │
│  Helpfulness                                                │
│  ☆ ☆ ☆ ☆ ☆                                                  │
│                                                              │
│  Punctuality                                                │
│  ☆ ☆ ☆ ☆ ☆                                                  │
│                                                              │
│  Friendliness                                               │
│  ☆ ☆ ☆ ☆ ☆                                                  │
│                                                              │
│  Overall: 0.0/5 (auto-calculated)                            │
│                                                              │
│  Write a review (optional, max 500 chars)                    │
│  ┌─────────────────────────────────────┐                   │
│  │ Mai was amazing...                  │                   │
│  │                            0/500   │                   │
│  └─────────────────────────────────────┘                   │
│                                                              │
│  Would you book again?                                       │
│  ( ) Yes!  ( ) Maybe  ( ) Probably not                      │
│                                                              │
│  ┌─────────────────────────────────────┐                   │
│  │       Submit rating                 │                   │
│  └─────────────────────────────────────┘                   │
│                                                              │
│  You can skip — we'll auto-rate 5⭐ after 7 days.          │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

**Trigger:** Hiện modal ngay sau khi session end (trong 5 phút đầu).

---

## 23. My Wallet

**Mục đích:** Xem balance + lịch sử giao dịch.

```
┌─────────────────────────────────────────────────────────────┐
│  ← Wallet                                                   │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                                                      │   │
│  │          🌱  75 seeds                                │   │
│  │                                                      │   │
│  │      ≈ $7.50 USD value (or 1 hour of teaching)      │   │
│  │                                                      │   │
│  │      Total earned: 145  •  Total spent: 70           │   │
│  │                                                      │   │
│  │      [Buy Seed Pack]   [Get free seeds →]            │   │
│  │                                                      │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                              │
│  ⚠️ 30 seeds expiring in 14 days (Sep 25)                   │
│     [Use them →]                                             │
│                                                              │
│  ── Transaction history ──                                  │
│                                                              │
│  Filter: [All ▼] [Earn] [Spend] [Expiring]                  │
│                                                              │
│  Sep 11  ✓ Session completed with Mai Tran                  │
│           +60 seeds  (earned)  expires Mar 11, 2027          │
│                                                              │
│  Sep 11  Session with Mai Tran                              │
│           −60 seeds  (spent)                                │
│                                                              │
│  Sep 10  ✗ Booking cancelled (within 24h)                   │
│           +30 seeds  (refund, 50%)                          │
│                                                              │
│  Sep 08  Welcome bonus                                       │
│           +30 seeds  (grant)  expires Mar 8, 2027            │
│                                                              │
│  [Load more]                                                 │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 24. My Profile (Edit)

**Mục đích:** Cập nhật Skill DNA bất kỳ lúc nào.

```
┌─────────────────────────────────────────────────────────────┐
│  ← Profile                                       [Edit] [⚙] │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌────────┐                                                  │
│  │  [👤] │  Mai Tran              🌳 Tree  ✓ Verified       │
│  │        │  ⭐ 4.9 (23 sessions)                            │
│  └────────┘  🌱 75 seeds                                    │
│                                                              │
│  "Product designer passionate about..."                      │
│                                                              │
│  ── Skills I teach (2) ──            [+ Add]                │
│  • Public Speaking (Lvl 3, 5y)      [Edit] [×]             │
│  • Python (Lvl 4, 3y)               [Edit] [×]             │
│                                                              │
│  ── Skills I want to learn (3) ──    [+ Add]                │
│  • System Design (Prio 4)           [Edit] [×]             │
│  • UX Research (Prio 3)             [Edit] [×]             │
│  • Business English (Prio 3)        [Edit] [×]             │
│                                                              │
│  ── Availability ──                  [Edit]                  │
│  18 hours/week                                          ⚙   │
│                                                              │
│  ── Languages ──                     [Edit]                  │
│  Vietnamese, English                                     ⚙   │
│                                                              │
│  ── Country ──                       [Edit]                  │
│  🇻🇳 Vietnam                                               ⚙   │
│                                                              │
│  ── Public profile ──                                        │
│  [View public page →]                                        │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 25. Notifications

```
┌─────────────────────────────────────────────────────────────┐
│  ← Notifications                          [Mark all read]   │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Today                                                       │
│                                                              │
│  🟢 [Avatar] Mai Tran accepted your booking                  │
│     "Public Speaking, Sep 11 19:00"                         │
│     10 min ago                          [View]              │
│                                                              │
│  ⏰ Reminder: Session with Mai Tran starts in 1 hour         │
│     Sep 11, 18:00                  [Join now →]              │
│                                                              │
│  ── Yesterday ──                                            │
│                                                              │
│  🎉 You've been matched with 3 new mentors this week!        │
│     [See matches →]                                         │
│                                                              │
│  ⭐ Linh N. left you a 5-star review                        │
│     "Amazing mentor!"                  [View profile]        │
│                                                              │
│  ── This week ──                                            │
│                                                              │
│  🌱 30 starter seeds added to your wallet                   │
│     Welcome to SkillSeed!                                    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

# PART D — ADMIN & B2B

## 26. Admin Dashboard

**Mục đích:** Admin theo dõi health metrics + moderate.

```
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed Admin                              [Logout]   │
├─────────────────────────────────────────────────────────────┤
│  📊 Dashboard  👥 Users  📅 Bookings  ⭐ Ratings           │
│  🎓 Skills  🚩 Reports  ⚙ Settings                         │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─ Today ──────────────┐  ┌─ This week ─────────────┐   │
│  │ DAU:        1,234    │  │ Sessions:     567       │   │
│  │ New users:   87      │  │ New bookings: 412       │   │
│  │ Sessions:     78     │  │ Revenue:     $890       │   │
│  │ Errors:       2      │  │ Churn:        3.2%      │   │
│  └───────────────────────┘  └─────────────────────────┘   │
│                                                              │
│  ── Sessions over time ──                                   │
│  ┌─────────────────────────────────────────────────────┐   │
│  │   ▁▂▃▄▅▆▇█▇▆▅▄▃▂▁                              │   │
│  │   Mon Tue Wed Thu Fri Sat Sun                        │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                              │
│  ── Top skills this week ──                                  │
│  1. Public Speaking    ████████████  124                    │
│  2. Python             ██████████     98                    │
│  3. UX Research        ███████        67                    │
│  4. Business English   █████          45                    │
│  5. Yoga               ████           34                    │
│                                                              │
│  ── Anomalies (3) ──                                         │
│  ⚠️ User "abc123" booked 12 sessions in 1h → flagged       │
│  ⚠️ Rating ring detected: 2 users mutually rated 5⭐       │
│  ⚠️ 5 custom skills pending review                          │
│     [Review queue →]                                        │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 27. B2B Org Dashboard

**Mục đích:** HR Manager quản lý skill exchange trong công ty.

```
┌─────────────────────────────────────────────────────────────┐
│  🏢 ACME Corp                  Mai Tran (HR) [Logout]       │
├─────────────────────────────────────────────────────────────┤
│  📊 Overview  👥 Members  🎓 Skills  📈 Reports  ⚙ Settings│
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─ This month ─────────────────────────────────────┐      │
│  │  Active members:    78/120 (65%)                  │      │
│  │  Sessions completed: 142                           │      │
│  │  Skills exchanged:  24 unique                     │      │
│  │  Avg rating:        4.6 ⭐                         │      │
│  └───────────────────────────────────────────────────┘      │
│                                                              │
│  ── Skills gap heatmap ──                                   │
│                                                              │
│             Eng  Design  Product  Sales  Ops                │
│  Junior    🟢    🟡     🟡       🟢    🟢                  │
│  Mid       🟢    🟢     🔴       🟡    🟢                  │
│  Senior    🟢    🟢     🟢       🔴    🟡                  │
│                                                              │
│  🟢 Strong (≥5 mentors internal)                            │
│  🟡 Moderate (1–4 mentors)                                   │
│  🔴 Gap (0 mentors) — recommend hiring or external           │
│                                                              │
│  ── Top contributors (this month) ──                        │
│  1. An Nguyen      12 sessions taught                        │
│  2. Duc Mai        10 sessions taught                        │
│  3. Linh Tran       9 sessions taught                        │
│  [+ see all 24 contributors]                                │
│                                                              │
│  ── Recommended actions ──                                   │
│  • 3 members haven't logged in for 30 days → nudge          │
│  • Product-Mid gap → consider external mentors                │
│  • Onboard 5 new members from invite link                    │
│                                                              │
│  [Export report (PDF)]  [Schedule 1-1 with admin]            │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

# Phụ lục — Design Tokens & Components

## Color palette

| Name | Hex | Use |
|------|-----|-----|
| Primary | `#10B981` | CTAs, brand accents |
| Primary-dark | `#047857` | Hover states |
| Secondary | `#6366F1` | Links, info |
| Success | `#10B981` | Confirmations |
| Warning | `#F59E0B` | Cautions |
| Danger | `#EF4444` | Errors, destructive |
| Text-primary | `#111827` | Body |
| Text-muted | `#6B7280` | Helper text |
| Background | `#F9FAFB` | Page bg |
| Card | `#FFFFFF` | Card bg |
| Border | `#E5E7EB` | Dividers |

## Typography

- **Font:** Inter (sans-serif), fallback system-ui
- **H1:** 32px / 600
- **H2:** 24px / 600
- **H3:** 20px / 600
- **Body:** 16px / 400
- **Small:** 14px / 400
- **Caption:** 12px / 400

## Spacing scale

- 4, 8, 12, 16, 24, 32, 48, 64 px

## Component library

- Buttons: primary, secondary, ghost, danger, icon
- Inputs: text, select, multiselect, search, textarea
- Cards: basic, hover-elevate, gradient
- Modals: confirm, form, full-screen (mobile)
- Toasts: success, error, info, warning
- Loaders: skeleton, spinner, progress
- Empty states: illustration + CTA

## Accessibility

- All interactive elements keyboard navigable.
- Focus ring visible.
- ARIA labels on icons.
- Color contrast ≥ 4.5:1 (WCAG AA).
- Touch targets ≥ 44×44 px (mobile).
- Form labels associated.

---

**Hoàn thành toàn bộ wireframes (27 screens + design tokens).**

**Tiếp theo sẽ là file `user-research-script.md` (research methodology + interview scripts + survey templates). Bạn muốn tôi tiếp tục ngay?**