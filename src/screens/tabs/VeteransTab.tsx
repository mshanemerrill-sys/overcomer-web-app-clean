import { useState } from 'react'
import {
  BookOpen, Check, ChevronLeft, ChevronRight, ClipboardCheck, Copy,
  ExternalLink, Medal, MessageCircle, Phone, Save, Shield, Star, Users
} from 'lucide-react'

type VeteranTab = 'guidance' | 'experts' | 'truths' | 'toolbox'

const focusOptions = [
  {
    label: 'PTSD & Anxiety',
    title: 'Managing PTSD & Hypervigilance',
    body: 'Your brain was trained to recognize and alert on potential threats. In civilian settings, that response can trigger false alarms. Ground your mind in Jesus—He is your fortress.'
  },
  {
    label: 'Moral Injury',
    title: 'Resolving Moral Guilt & Sorrow',
    body: "Combat or operational stress can leave deep spiritual wounds. Your past is covered by Christ's perfect blood. In Him, you are clean and made new."
  },
  {
    label: 'Transition Fatigue',
    title: 'Navigating Transition Fatigue',
    body: 'Leaving a highly structured, close-knit unit can feel isolating. Create small daily missions for spiritual growth and stay connected to a healthy church community.'
  },
  {
    label: 'Other',
    title: 'Custom Reflection Focus',
    body: 'Name what you are navigating today. Bringing it into the light is a courageous first step toward support and renewed strength.'
  }
]

const guidance = [
  {
    title: 'Managing Hypervigilance & PTSD',
    body: 'Your alert system learned to protect you. When it sounds a false alarm, orient to the present: name where you are, slow your breathing, and remind yourself that the Lord is your refuge.',
    scripture: '“God is our refuge and strength, an ever-present help in trouble.” — Psalm 46:1'
  },
  {
    title: 'Restoring Mission & Purpose',
    body: 'Your uniform was an assignment, not your entire identity. Ask God for one clear mission today: serve your family, encourage a brother or sister, build a skill, or strengthen your church.',
    scripture: '“We are God’s handiwork, created in Christ Jesus to do good works.” — Ephesians 2:10'
  },
  {
    title: 'Overcoming Moral Injury & Guilt',
    body: 'What you carried in service matters, and Christ does not minimize it. He invites confession, forgiveness, lament, and restoration in a trusted community. Shame is not your permanent commander.',
    scripture: '“There is now no condemnation for those who are in Christ Jesus.” — Romans 8:1'
  }
]

const experts = [
  {
    name: 'Chad Robichaux — Mighty Oaks Foundation',
    body: 'Faith-based, peer-led intensive programs for veterans and first responders facing trauma and transition.',
    href: 'https://www.mightyoaksprograms.org'
  },
  {
    name: 'Evan Owens — REBOOT Recovery',
    body: 'Trauma-healing courses that help veterans and families rebuild through faith and community.',
    href: 'https://rebootrecovery.com'
  },
  {
    name: 'Dr. H. Norman Wright',
    body: 'Biblical grief, trauma, and crisis-care guidance for processing loss and major life transitions.'
  },
  {
    name: 'Chaplain Doug Carver',
    body: 'Pastoral leadership focused on service members, military families, spiritual readiness, and reintegration.'
  }
]

const truths = [
  {
    lie: 'My best years are behind me, and I lost my purpose when I took off the uniform.',
    truth: 'God has not retired my calling. I am commissioned by King Jesus for a new mission.',
    scripture: 'Ephesians 2:10'
  },
  {
    lie: 'If I ask for help, I am weak.',
    truth: 'Calling for reinforcement is wisdom. God designed His people to carry one another’s burdens.',
    scripture: 'Galatians 6:2'
  },
  {
    lie: 'No civilian could ever understand me, so I have to carry this alone.',
    truth: 'Christ understands suffering completely, and He provides trustworthy people who can listen and walk beside me.',
    scripture: 'Hebrews 4:15-16'
  },
  {
    lie: 'What happened in service has put me beyond forgiveness.',
    truth: 'Christ’s finished work is greater than my darkest memory. Confession opens the way to cleansing and restoration.',
    scripture: '1 John 1:9'
  }
]

const bulletins = [
  'New mission: take one faithful step today. Small, completed missions rebuild momentum.',
  'Buddy check: contact one person who served with you or understands the road you are walking.',
  'Stand firm: pause, notice five things around you, and remind your body that you are here and safe.',
  'After action review: name what went well, what was difficult, and the next wise adjustment.',
  'Your service is part of your story—not the limit of God’s purpose for your future.'
]

export default function VeteransTab() {
  const [tab, setTab] = useState<VeteranTab>('guidance')
  const [focus, setFocus] = useState(0)
  const [customFocus, setCustomFocus] = useState('')

  return (
    <div className="p-4 space-y-4">
      <section className="rounded-2xl border border-green-800/30 bg-white p-4">
        <div className="flex items-start gap-3">
          <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-green-900 text-white">
            <Star className="h-6 w-6" />
          </span>
          <div>
            <h3 className="text-sm font-extrabold uppercase tracking-[0.12em] text-green-900">
              The Next Mission: Veteran Support &amp; Wellness
            </h3>
            <p className="mt-2 text-sm leading-relaxed text-gray-600">
              You were trained to endure the hardest battles, but you do not have to fight the invisible ones alone. Seeking support is not stepping down—it is stepping up.
            </p>
          </div>
        </div>
      </section>

      <TargetedFocus
        selected={focus}
        onSelect={setFocus}
        customFocus={customFocus}
        onCustomFocus={setCustomFocus}
      />

      <div className="flex gap-2 overflow-x-auto pb-1">
        {([
          ['guidance', 'Guidance'],
          ['experts', 'Experts'],
          ['truths', 'Truths'],
          ['toolbox', 'Toolbox']
        ] as const).map(([id, label]) => (
          <button
            key={id}
            onClick={() => setTab(id)}
            className={`shrink-0 rounded-full px-4 py-2 text-sm font-bold transition-colors ${
              tab === id ? 'bg-green-900 text-white' : 'bg-green-50 text-green-900'
            }`}
          >
            {label}
          </button>
        ))}
      </div>

      {tab === 'guidance' && <Guidance />}
      {tab === 'experts' && <Experts />}
      {tab === 'truths' && <Truths />}
      {tab === 'toolbox' && <VeteranToolbox />}

      <ImmediateSupport />
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
    <section className="rounded-2xl border border-green-800/25 bg-green-50/60 p-4">
      <h4 className="text-xs font-extrabold uppercase tracking-widest text-green-900">Choose Your Focus</h4>
      <div className="mt-3 flex gap-2 overflow-x-auto pb-1">
        {focusOptions.map((option, index) => (
          <button
            key={option.label}
            onClick={() => onSelect(index)}
            className={`shrink-0 rounded-xl px-3 py-2 text-xs font-bold ${selected === index ? 'bg-green-900 text-white' : 'bg-white text-green-900'}`}
          >
            {option.label}
          </button>
        ))}
      </div>
      <h5 className="mt-4 font-bold text-green-950">{item.title}</h5>
      <p className="mt-1 text-sm leading-relaxed text-gray-700">{item.body}</p>
      {selected === 3 && (
        <textarea
          value={customFocus}
          onChange={event => onCustomFocus(event.target.value)}
          rows={3}
          className="mt-3 w-full rounded-xl border border-green-800/25 bg-white p-3 text-sm outline-none focus:ring-2 focus:ring-green-700"
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
          <h4 className="flex items-center gap-2 font-bold text-green-950">
            <Shield className="h-4 w-4 text-green-800" />
            {item.title}
          </h4>
          <p className="mt-2 text-sm leading-relaxed text-gray-600">{item.body}</p>
          <p className="mt-3 rounded-xl bg-green-50 p-3 text-xs font-semibold italic leading-relaxed text-green-900">{item.scripture}</p>
        </article>
      ))}
    </div>
  )
}

function Experts() {
  return (
    <div className="space-y-3">
      <div className="rounded-xl bg-green-50 p-3 text-sm text-green-950">
        These trusted leaders and programs bridge biblical discipleship, trauma support, and veteran community.
      </div>
      {experts.map(expert => (
        <article key={expert.name} className="rounded-2xl border border-gray-100 bg-white p-4 shadow-sm">
          <h4 className="font-bold text-gray-900">{expert.name}</h4>
          <p className="mt-1 text-sm leading-relaxed text-gray-600">{expert.body}</p>
          {expert.href && (
            <a href={expert.href} target="_blank" rel="noopener noreferrer" className="mt-3 inline-flex items-center gap-1 text-sm font-bold text-green-800">
              Visit program portal <ExternalLink className="h-3.5 w-3.5" />
            </a>
          )}
        </article>
      ))}
      <ResourceLink href="https://www.va.gov/find-locations" label="Find a VA Vet Center" />
      <ResourceLink href="https://www.veteranscrisisline.net" label="Veterans Crisis Line" />
    </div>
  )
}

function Truths() {
  const [open, setOpen] = useState<number | null>(0)

  return (
    <div className="space-y-3">
      {truths.map((item, index) => (
        <article key={item.lie} className="overflow-hidden rounded-2xl border border-gray-100 bg-white shadow-sm">
          <button onClick={() => setOpen(open === index ? null : index)} className="w-full p-4 text-left">
            <span className="text-xs font-bold uppercase tracking-wide text-red-500">The lie</span>
            <p className="mt-1 text-sm italic text-gray-700">“{item.lie}”</p>
          </button>
          {open === index && (
            <div className="border-t border-gray-100 bg-green-50 p-4">
              <span className="text-xs font-bold uppercase tracking-wide text-green-800">God’s truth</span>
              <p className="mt-1 text-sm font-semibold leading-relaxed text-green-950">{item.truth}</p>
              <p className="mt-2 text-xs font-bold text-green-800">{item.scripture}</p>
            </div>
          )}
        </article>
      ))}
    </div>
  )
}

function VeteranToolbox() {
  const [aar, setAar] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('overcomer_veteran_aar') || 'null') || { win: '', challenge: '', adjustment: '' }
    } catch {
      return { win: '', challenge: '', adjustment: '' }
    }
  })
  const [saved, setSaved] = useState(false)
  const [grounded, setGrounded] = useState<boolean[]>([false, false, false, false, false])
  const [buddyDone, setBuddyDone] = useState(false)
  const [copied, setCopied] = useState(false)
  const [bulletin, setBulletin] = useState(0)

  const saveAar = () => {
    localStorage.setItem('overcomer_veteran_aar', JSON.stringify(aar))
    setSaved(true)
  }

  const copyMessage = async () => {
    await navigator.clipboard.writeText('Buddy check: You were on my mind today. No pressure to explain anything—just checking that you are okay. I am here if you want to talk.')
    setCopied(true)
  }

  return (
    <div className="space-y-4">
      <ToolCard icon={<ClipboardCheck className="h-5 w-5" />} title="Daily After Action Review (A.A.R.)">
        <p className="text-xs leading-relaxed text-gray-600">Review the day with grace: keep what worked, learn from what did not, and identify the next wise adjustment.</p>
        <AarField label="What was today’s win?" value={aar.win} onChange={value => { setAar({ ...aar, win: value }); setSaved(false) }} />
        <AarField label="What was difficult?" value={aar.challenge} onChange={value => { setAar({ ...aar, challenge: value }); setSaved(false) }} />
        <AarField label="What will I adjust tomorrow?" value={aar.adjustment} onChange={value => { setAar({ ...aar, adjustment: value }); setSaved(false) }} />
        <button onClick={saveAar} className="flex w-full items-center justify-center gap-2 rounded-xl bg-green-900 py-2.5 text-sm font-bold text-white">
          <Save className="h-4 w-4" /> {saved ? 'Reflection Saved' : 'Save Reflection'}
        </button>
      </ToolCard>

      <ToolCard icon={<Shield className="h-5 w-5" />} title="5-4-3-2-1 “Stand Firm” Drill">
        <p className="text-xs leading-relaxed text-gray-600">Reconnect with the present. Complete each observation slowly while breathing at a steady pace.</p>
        {['5 things I can see', '4 things I can feel', '3 things I can hear', '2 things I can smell', '1 truth from God I can speak'].map((label, index) => (
          <label key={label} className="flex items-center gap-3 rounded-xl bg-green-50 p-3 text-sm font-semibold text-green-950">
            <input type="checkbox" checked={grounded[index]} onChange={() => setGrounded(items => items.map((item, i) => i === index ? !item : item))} className="h-4 w-4 accent-green-800" />
            {label}
          </label>
        ))}
      </ToolCard>

      <ToolCard icon={<Users className="h-5 w-5" />} title="Buddy Check Protocol">
        <p className="text-sm text-gray-600">Check on a battle buddy—or let someone know you need a check-in. Connection is tactical wisdom.</p>
        <div className="rounded-xl bg-gray-50 p-3 text-xs italic leading-relaxed text-gray-700">
          “You were on my mind today. No pressure to explain anything—just checking that you are okay. I am here if you want to talk.”
        </div>
        <div className="grid grid-cols-2 gap-2">
          <button onClick={() => void copyMessage()} className="flex items-center justify-center gap-2 rounded-xl border border-green-800 px-3 py-2 text-sm font-bold text-green-900">
            <Copy className="h-4 w-4" /> {copied ? 'Copied' : 'Copy Text'}
          </button>
          <button onClick={() => setBuddyDone(!buddyDone)} className="flex items-center justify-center gap-2 rounded-xl bg-green-900 px-3 py-2 text-sm font-bold text-white">
            <Check className="h-4 w-4" /> {buddyDone ? 'Completed' : 'Mark Done'}
          </button>
        </div>
      </ToolCard>

      <ToolCard icon={<BookOpen className="h-5 w-5" />} title="Daily Veteran Bulletin">
        <p className="rounded-xl bg-green-50 p-4 text-sm font-semibold leading-relaxed text-green-950">{bulletins[bulletin]}</p>
        <div className="flex items-center justify-between">
          <button onClick={() => setBulletin((bulletin - 1 + bulletins.length) % bulletins.length)} className="rounded-full bg-gray-100 p-2" aria-label="Previous bulletin"><ChevronLeft className="h-4 w-4" /></button>
          <span className="text-xs font-bold text-gray-500">{bulletin + 1} / {bulletins.length}</span>
          <button onClick={() => setBulletin((bulletin + 1) % bulletins.length)} className="rounded-full bg-gray-100 p-2" aria-label="Next bulletin"><ChevronRight className="h-4 w-4" /></button>
        </div>
      </ToolCard>
    </div>
  )
}

function AarField({ label, value, onChange }: { label: string; value: string; onChange: (value: string) => void }) {
  return (
    <label className="block text-xs font-bold text-gray-700">
      {label}
      <textarea value={value} onChange={event => onChange(event.target.value)} rows={2} className="mt-1 w-full rounded-xl border border-gray-200 p-2 text-sm font-normal outline-none focus:ring-2 focus:ring-green-700" />
    </label>
  )
}

function ToolCard({ icon, title, children }: { icon: React.ReactNode; title: string; children: React.ReactNode }) {
  return (
    <section className="space-y-3 rounded-2xl border border-green-800/20 bg-white p-4 shadow-sm">
      <h4 className="flex items-center gap-2 font-bold text-green-950">{icon}{title}</h4>
      {children}
    </section>
  )
}

function ResourceLink({ href, label }: { href: string; label: string }) {
  return (
    <a href={href} target="_blank" rel="noopener noreferrer" className="flex items-center justify-between rounded-xl bg-green-900 p-3 text-sm font-bold text-white">
      {label}<ExternalLink className="h-4 w-4" />
    </a>
  )
}

function ImmediateSupport() {
  return (
    <section className="rounded-2xl border border-green-800/30 bg-green-50 p-4 text-center">
      <Medal className="mx-auto h-6 w-6 text-green-900" />
      <h4 className="mt-2 font-bold text-green-950">Need Immediate Assistance?</h4>
      <p className="mt-1 text-xs text-green-900">The Veterans Crisis Line is confidential and available 24/7.</p>
      <a href="tel:988" className="mt-3 flex w-full items-center justify-center gap-2 rounded-xl bg-green-900 py-3 text-sm font-bold text-white">
        <Phone className="h-4 w-4" /> Call 988, then press 1
      </a>
      <a href="sms:838255" className="mt-2 flex w-full items-center justify-center gap-2 rounded-xl border border-green-900 py-3 text-sm font-bold text-green-900">
        <MessageCircle className="h-4 w-4" /> Text 838255
      </a>
    </section>
  )
}
