import { useState } from 'react'
import {
  BookOpen, Check, ChevronLeft, ChevronRight, CirclePlus, ExternalLink,
  RefreshCw, Shield, Target, Users
} from 'lucide-react'

type ReentryTabId = 'guidance' | 'experts' | 'toolbox'

const focusOptions = [
  {
    label: 'Structure & Habits',
    title: 'Managing Institutional Habits',
    body: 'Transitioning to ordinary life can make schedule changes feel disorienting. God is your ultimate orchestrator of time; build flexible routines anchored in His peace.'
  },
  {
    label: 'Decision Fatigue',
    title: 'Overcoming Decision Fatigue',
    body: 'When choices were once made for you, everyday options can feel paralyzing. Breathe slowly, make one simple decision at a time, and remember that Christ holds your hand.'
  },
  {
    label: 'Boundary Building',
    title: 'Constructing Healthy Family Boundaries',
    body: 'Rebuilding relationships requires wisdom and structure. Honest, compassionate biblical boundaries guard your focus while trust grows over time.'
  },
  {
    label: 'Other',
    title: 'Custom Reflection Focus',
    body: 'Describe the transition challenge in front of you. You can turn a vague burden into one clear next step.'
  }
]

const guidance = [
  {
    title: 'Managing Institutional Structure Habits',
    body: 'Use a simple morning, midday, and evening rhythm instead of trying to control every minute. Keep anchors—Scripture, work, meals, rest, and community—while allowing healthy flexibility.',
    action: 'Choose three anchors for tomorrow and write them into your 24-hour plan.'
  },
  {
    title: 'Overcoming Decision Fatigue',
    body: 'Limit your options when the number of choices feels overwhelming. Ask: Is it wise? Is it lawful? Does it support the person God is shaping me to become?',
    action: 'Make the smallest safe decision first, then pause before the next one.'
  },
  {
    title: 'Constructing Healthy Family Boundaries',
    body: 'Reconnection does not require instant access or instant trust. Clear boundaries protect both people and make honest restoration possible.',
    action: 'Name one relationship that belongs in your inner, middle, or outer circle.'
  }
]

const experts = [
  {
    name: 'Chuck Colson & Prison Fellowship',
    body: 'A pioneer of modern Christian prison ministry. Born Again and Prison Fellowship’s reentry resources provide practical spiritual structure for returning citizens.',
    href: 'https://www.prisonfellowship.org/resources/'
  },
  {
    name: 'Drs. Henry Cloud & John Townsend — Boundaries',
    body: 'Biblical tools for relational health, personal responsibility, and protection from toxic dynamics or former negative influences.'
  },
  {
    name: 'Pastor Craig Groeschel',
    body: 'Practical Scripture-centered thought reframing that helps replace limiting patterns with truth and intentional action.',
    href: 'https://www.craiggroeschel.com'
  },
  {
    name: 'Dr. Gregory Jantz',
    body: 'Whole-person Christian care addressing the physical, emotional, relational, and spiritual effects of trauma and chemical struggles.'
  },
  {
    name: 'Drs. Kenneth Robinson & Gregory Little',
    body: 'Personal-responsibility and moral-reasoning tools designed for justice-involved people, with practical steps for healthier decisions.'
  },
  {
    name: 'Dr. Viktor Frankl — Man’s Search for Meaning',
    body: 'A powerful foundation for finding meaning, responsibility, and resilience after confinement and suffering.'
  }
]

const restorationFeed = [
  'Your past is part of your testimony, not the authority over your future. Christ has the final word.',
  'Build slowly and honestly today. One kept promise strengthens trust—with others and with yourself.',
  'Structure serves you; it does not own you. Leave room for wisdom, rest, and God-directed change.',
  'A healthy boundary is not rejection. It is a clear path for safe, truthful relationship.',
  'You do not have to solve your entire future today. Complete the next faithful step.'
]

export default function ReentryTab() {
  const [tab, setTab] = useState<ReentryTabId>('guidance')
  const [focus, setFocus] = useState(0)
  const [customFocus, setCustomFocus] = useState('')

  return (
    <div className="p-4 space-y-4">
      <section className="rounded-2xl border border-indigo-800/25 bg-white p-4">
        <div className="flex items-start gap-3">
          <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-[#3F51B5] text-white">
            <RefreshCw className="h-6 w-6" />
          </span>
          <div>
            <h3 className="text-sm font-extrabold uppercase tracking-[0.12em] text-[#303F9F]">Steps to Restoration &amp; Freedom</h3>
            <p className="mt-2 text-sm leading-relaxed text-gray-600">
              Transitioning to ordinary life after incarceration is a journey that requires strength, structure, and active redemptive community. Draw from targeted Scripture, mental exercises, and trusted biblical leaders below.
            </p>
          </div>
        </div>
      </section>

      <TargetedFocus selected={focus} onSelect={setFocus} customFocus={customFocus} onCustomFocus={setCustomFocus} />

      <div className="flex gap-2 overflow-x-auto pb-1">
        {([
          ['guidance', 'Guidance'],
          ['experts', 'Experts'],
          ['toolbox', 'Toolbox']
        ] as const).map(([id, label]) => (
          <button
            key={id}
            onClick={() => setTab(id)}
            className={`shrink-0 rounded-full px-4 py-2 text-sm font-bold transition-colors ${
              tab === id ? 'bg-[#3F51B5] text-white' : 'bg-indigo-50 text-[#303F9F]'
            }`}
          >
            {label}
          </button>
        ))}
      </div>

      {tab === 'guidance' && <Guidance />}
      {tab === 'experts' && <Experts />}
      {tab === 'toolbox' && <ReentryToolbox />}

      <a
        href="https://www.reentry.org"
        target="_blank"
        rel="noopener noreferrer"
        className="flex w-full items-center justify-center gap-2 rounded-2xl bg-[#3F51B5] px-4 py-3 text-sm font-bold text-white"
      >
        Re-entry support and resources
        <ExternalLink className="h-4 w-4" />
      </a>
    </div>
  )
}

function TargetedFocus({
  selected,
  onSelect,
  customFocus,
  onCustomFocus
}: {
  selected: number
  onSelect: (index: number) => void
  customFocus: string
  onCustomFocus: (value: string) => void
}) {
  const item = focusOptions[selected]

  return (
    <section className="rounded-2xl border border-indigo-800/20 bg-indigo-50/60 p-4">
      <h4 className="text-xs font-extrabold uppercase tracking-widest text-[#303F9F]">Choose Your Re-entry Focus</h4>
      <div className="mt-3 flex gap-2 overflow-x-auto pb-1">
        {focusOptions.map((option, index) => (
          <button
            key={option.label}
            onClick={() => onSelect(index)}
            className={`shrink-0 rounded-xl px-3 py-2 text-xs font-bold ${selected === index ? 'bg-[#3F51B5] text-white' : 'bg-white text-[#303F9F]'}`}
          >
            {option.label}
          </button>
        ))}
      </div>
      <h5 className="mt-4 font-bold text-[#283593]">{item.title}</h5>
      <p className="mt-1 text-sm leading-relaxed text-gray-700">{item.body}</p>
      {selected === 3 && (
        <textarea
          value={customFocus}
          onChange={event => onCustomFocus(event.target.value)}
          rows={3}
          className="mt-3 w-full rounded-xl border border-indigo-800/20 bg-white p-3 text-sm outline-none focus:ring-2 focus:ring-indigo-600"
          placeholder="Describe what you are navigating today..."
        />
      )}
    </section>
  )
}

function Guidance() {
  return (
    <div className="space-y-3">
      {guidance.map(item => (
        <article key={item.title} className="rounded-2xl border border-gray-100 bg-white p-4 shadow-sm">
          <h4 className="flex items-center gap-2 font-bold text-[#283593]"><Shield className="h-4 w-4" />{item.title}</h4>
          <p className="mt-2 text-sm leading-relaxed text-gray-600">{item.body}</p>
          <p className="mt-3 rounded-xl bg-indigo-50 p-3 text-xs font-semibold leading-relaxed text-[#303F9F]">Next step: {item.action}</p>
        </article>
      ))}
    </div>
  )
}

function Experts() {
  return (
    <div className="space-y-3">
      <div className="rounded-xl bg-indigo-50 p-3 text-sm leading-relaxed text-[#283593]">
        Biblical mentors are paired with practical foundations that support responsibility, meaning, healthy boundaries, and whole-person restoration.
      </div>
      {experts.map(expert => (
        <article key={expert.name} className="rounded-2xl border border-gray-100 bg-white p-4 shadow-sm">
          <h4 className="font-bold text-gray-900">{expert.name}</h4>
          <p className="mt-1 text-sm leading-relaxed text-gray-600">{expert.body}</p>
          {expert.href && (
            <a href={expert.href} target="_blank" rel="noopener noreferrer" className="mt-3 inline-flex items-center gap-1 text-sm font-bold text-[#3F51B5]">
              Visit resource <ExternalLink className="h-3.5 w-3.5" />
            </a>
          )}
        </article>
      ))}
    </div>
  )
}

function ReentryToolbox() {
  const [halt, setHalt] = useState<Record<string, boolean>>({ Hungry: false, Angry: false, Lonely: false, Tired: false })
  const [ring, setRing] = useState(0)
  const [thought, setThought] = useState('')
  const [processed, setProcessed] = useState(false)
  const [goals, setGoals] = useState([
    'Spend 10 minutes in prayer and reading Scripture',
    'Apply for one job, study, or complete a vocational task',
    'Attend an OverComer group, church service, or call a mentor',
    'Secure healthy rest and proper sleep'
  ])
  const [checkedGoals, setCheckedGoals] = useState<Set<string>>(new Set())
  const [customGoal, setCustomGoal] = useState('')
  const [feed, setFeed] = useState(0)

  const haltAdvice: Record<string, string> = {
    Hungry: 'Get a healthy meal. God designed your body to need physical fuel before your mind can settle.',
    Angry: 'Slow down before responding. Breathe and invite Jesus to align your heart with truth.',
    Lonely: 'Contact a mentor or trusted friend now. Isolation gives old patterns room to grow.',
    Tired: 'Choose safe rest. Elijah received food and sleep before God asked for his next step.'
  }

  const ringInfo = [
    ['Center — Full Trust', 'Safe, faith-filled people such as a pastor, mentor, sponsor, or healthy family member who actively build your faith and accountability.'],
    ['Middle — Necessary Connections', 'Work colleagues, neighbors, case workers, or other structured relationships built on respect without immediate deep vulnerability.'],
    ['Outer — Guarded Distance', 'People still engaged in destructive behavior. Love and pray for them while keeping safe distance and avoiding vulnerable settings.']
  ]

  const addGoal = () => {
    const goal = customGoal.trim()
    if (!goal) return
    setGoals(items => [...items, goal])
    setCustomGoal('')
  }

  return (
    <div className="space-y-4">
      <ToolCard icon={<Users className="h-5 w-5" />} title="The HALT Check-in Tool">
        <p className="text-xs leading-relaxed text-gray-600">Pause and see whether physical or emotional strain is masking itself as a spiritual setback.</p>
        <div className="grid grid-cols-2 gap-2">
          {Object.keys(halt).map(item => (
            <button key={item} onClick={() => setHalt(state => ({ ...state, [item]: !state[item] }))} className={`rounded-xl px-3 py-2 text-sm font-bold ${halt[item] ? 'bg-[#3F51B5] text-white' : 'bg-indigo-50 text-[#303F9F]'}`}>
              {item}
            </button>
          ))}
        </div>
        {Object.entries(halt).filter(([, active]) => active).map(([item]) => (
          <p key={item} className="rounded-xl bg-indigo-50 p-3 text-xs leading-relaxed text-[#283593]"><strong>{item}:</strong> {haltAdvice[item]}</p>
        ))}
      </ToolCard>

      <ToolCard icon={<Target className="h-5 w-5" />} title="The Boundaries Bullseye">
        <p className="text-xs leading-relaxed text-gray-600">Sort relationships into three circles so trust can be rebuilt safely.</p>
        <div className="grid grid-cols-3 gap-1">
          {['Inner', 'Middle', 'Outer'].map((label, index) => (
            <button key={label} onClick={() => setRing(index)} className={`rounded-lg px-2 py-2 text-xs font-bold ${ring === index ? 'bg-[#3F51B5] text-white' : 'bg-indigo-50 text-[#303F9F]'}`}>{label}</button>
          ))}
        </div>
        <div className="rounded-xl bg-gray-50 p-3">
          <p className="text-sm font-bold text-[#283593]">{ringInfo[ring][0]}</p>
          <p className="mt-1 text-xs leading-relaxed text-gray-600">{ringInfo[ring][1]}</p>
        </div>
      </ToolCard>

      <ToolCard icon={<RefreshCw className="h-5 w-5" />} title="“Take Every Thought Captive” — The 3 C’s">
        <p className="text-xs leading-relaxed text-gray-600">When a limiting lie enters your mind, catch it, check it against God’s Word, and change it with truth.</p>
        <textarea value={thought} onChange={event => { setThought(event.target.value); setProcessed(false) }} rows={3} className="w-full rounded-xl border border-gray-200 p-3 text-sm outline-none focus:ring-2 focus:ring-indigo-600" placeholder="Enter a negative or limiting lie..." />
        <button disabled={!thought.trim()} onClick={() => setProcessed(true)} className="w-full rounded-xl bg-[#3F51B5] py-2.5 text-sm font-bold text-white disabled:opacity-40">Apply the 3 C’s Filter</button>
        {processed && (
          <div className="space-y-2 rounded-xl bg-amber-50 p-3 text-xs leading-relaxed text-gray-700">
            <p><strong>1. Catch:</strong> “{thought}”</p>
            <p><strong>2. Check:</strong> Is this true according to God’s Word, or is my past speaking? Romans 8:1 says there is no condemnation in Christ.</p>
            <p className="font-semibold text-green-800"><strong>3. Change:</strong> “I am a new creation in Christ. The old has gone; the new is here.” — 2 Corinthians 5:17</p>
          </div>
        )}
      </ToolCard>

      <ToolCard icon={<Check className="h-5 w-5" />} title="My Next 24 Hours">
        <p className="text-xs leading-relaxed text-gray-600">Plan the next faithful day—not the whole future.</p>
        {goals.map(goal => (
          <label key={goal} className="flex items-start gap-3 rounded-xl bg-indigo-50 p-3 text-sm text-[#283593]">
            <input type="checkbox" checked={checkedGoals.has(goal)} onChange={() => setCheckedGoals(items => {
              const next = new Set(items)
              if (next.has(goal)) next.delete(goal); else next.add(goal)
              return next
            })} className="mt-0.5 h-4 w-4 accent-indigo-700" />
            <span className={checkedGoals.has(goal) ? 'line-through opacity-60' : ''}>{goal}</span>
          </label>
        ))}
        <div className="flex gap-2">
          <input value={customGoal} onChange={event => setCustomGoal(event.target.value)} onKeyDown={event => { if (event.key === 'Enter') addGoal() }} className="min-w-0 flex-1 rounded-xl border border-gray-200 px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-indigo-600" placeholder="Add a goal..." />
          <button onClick={addGoal} className="rounded-xl bg-[#3F51B5] p-2.5 text-white" aria-label="Add goal"><CirclePlus className="h-5 w-5" /></button>
        </div>
      </ToolCard>

      <ToolCard icon={<BookOpen className="h-5 w-5" />} title="Daily Restoration Feed">
        <p className="rounded-xl bg-indigo-50 p-4 text-sm font-semibold leading-relaxed text-[#283593]">{restorationFeed[feed]}</p>
        <div className="flex items-center justify-between">
          <button onClick={() => setFeed((feed - 1 + restorationFeed.length) % restorationFeed.length)} className="rounded-full bg-gray-100 p-2" aria-label="Previous reminder"><ChevronLeft className="h-4 w-4" /></button>
          <span className="text-xs font-bold text-gray-500">{feed + 1} / {restorationFeed.length}</span>
          <button onClick={() => setFeed((feed + 1) % restorationFeed.length)} className="rounded-full bg-gray-100 p-2" aria-label="Next reminder"><ChevronRight className="h-4 w-4" /></button>
        </div>
      </ToolCard>
    </div>
  )
}

function ToolCard({ icon, title, children }: { icon: React.ReactNode; title: string; children: React.ReactNode }) {
  return (
    <section className="space-y-3 rounded-2xl border border-indigo-800/20 bg-white p-4 shadow-sm">
      <h4 className="flex items-center gap-2 font-bold text-[#283593]">{icon}{title}</h4>
      {children}
    </section>
  )
}
