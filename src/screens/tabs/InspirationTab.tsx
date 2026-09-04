import { useState } from 'react'
import { inspirationQuotes } from '../../lib/data'
import { Quote, Heart, RefreshCw, Moon, Shield, Sparkles, Crown, FileText, Download, BookOpen, ChevronDown, ChevronUp, Users } from 'lucide-react'
import { useAppStore } from '../../store/useAppStore'
import type { InspirationQuote } from '../../lib/types'

type Category = 'OVERCOMING_CRAVINGS' | 'PEACE_ANXIETY' | 'STRENGTH_FAITH' | 'GRACE_FORGIVENESS' | 'IAM_DECLARATIONS'

const categoryInfo: Record<Category, { label: string; icon: React.ReactNode; color: string }> = {
  IAM_DECLARATIONS: { label: 'I AM Declarations', icon: <Crown className="w-5 h-5" />, color: 'bg-accent-gold' },
  OVERCOMING_CRAVINGS: { label: 'Fight From Victory', icon: <Shield className="w-5 h-5" />, color: 'bg-accent-teal' },
  PEACE_ANXIETY: { label: 'Peace & Anxiety', icon: <Moon className="w-5 h-5" />, color: 'bg-primary-400' },
  STRENGTH_FAITH: { label: 'Strength & Faith', icon: <Sparkles className="w-5 h-5" />, color: 'bg-secondary-500' },
  GRACE_FORGIVENESS: { label: 'Grace & Forgiveness', icon: <Heart className="w-5 h-5" />, color: 'bg-accent-coral' }
}

export default function InspirationTab({ onNavigateToCompanion }: { onNavigateToCompanion: () => void }) {
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null)
  const { setPendingCompanionMessage, userPath } = useAppStore()

  const categoriesForPath: Partial<Record<NonNullable<typeof userPath>, Category[]>> = {
    SUBSTANCE_RECOVERY: ['OVERCOMING_CRAVINGS', 'GRACE_FORGIVENESS', 'STRENGTH_FAITH'],
    MENTAL_HEALTH: ['PEACE_ANXIETY', 'STRENGTH_FAITH', 'GRACE_FORGIVENESS'],
    TOUGH_DAY: ['PEACE_ANXIETY', 'STRENGTH_FAITH'],
    VETERAN_TRANSITION: ['STRENGTH_FAITH', 'PEACE_ANXIETY'],
    REENTRY_RESTORATION: ['GRACE_FORGIVENESS', 'STRENGTH_FAITH']
  }

  const relevantCategories = userPath ? categoriesForPath[userPath] : undefined

  const filteredQuotes = inspirationQuotes.filter(q =>
    (!selectedCategory || q.category === selectedCategory) &&
    (!relevantCategories || relevantCategories.includes(q.category))
  )

  const categories = (Object.keys(categoryInfo) as Category[]).filter(category =>
    !relevantCategories || relevantCategories.includes(category)
  )

  const handleRenewMind = (quote: InspirationQuote) => {
    setPendingCompanionMessage(
      `I want to renew my mind on this scripture: "${quote.text}" — ${quote.reference}. Please help me by: (1) giving me a personal declaration I can speak aloud based on this truth, (2) a short reflection on how this applies to my life right now, and (3) a prayer I can pray to seal this truth in my heart.`
    )
    onNavigateToCompanion()
  }

  return (
    <div className="p-4 pb-8">
      {/* Header */}
      <div className="text-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Inspiration</h1>
        <p className="text-sm text-gray-600 mt-1">Declare who God says you are. Renew your mind in His truth.</p>
      </div>

      {/* Category Filters */}
      <div className="flex gap-2 overflow-x-auto pb-4 scrollbar-hide">
        <button
          onClick={() => setSelectedCategory(null)}
          className={`flex-shrink-0 px-4 py-2 rounded-full font-medium text-sm transition-colors ${
            !selectedCategory
              ? 'bg-gray-900 text-white'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          }`}
        >
          All
        </button>
        {categories.map(cat => (
          <button
            key={cat}
            onClick={() => setSelectedCategory(cat)}
            className={`flex-shrink-0 flex items-center gap-2 px-4 py-2 rounded-full font-medium text-sm transition-colors ${
              selectedCategory === cat
                ? 'bg-gray-900 text-white'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            {categoryInfo[cat].icon}
            {categoryInfo[cat].label}
          </button>
        ))}
      </div>

      {/* I AM Banner (shown when that category is selected or at top of All) */}
      {(selectedCategory === 'IAM_DECLARATIONS' || !selectedCategory) && (
        <div className="bg-gradient-to-br from-accent-gold/20 to-primary-50 rounded-2xl p-5 mb-4 border border-accent-gold/30">
          <p className="text-center font-black text-lg text-gray-900 leading-snug">
            I AM Loved By God.
          </p>
          <p className="text-center font-bold text-gray-700 text-sm mt-1">
            I AM NOT Who Others Say I Am.
          </p>
          <p className="text-center font-bold text-gray-700 text-sm">
            I AM NOT Who I Used To Be.
          </p>
          <p className="text-center font-black text-primary-600 text-sm mt-1">
            I AM Who God Says I Am.
          </p>
          <p className="text-center text-xs text-gray-500 mt-2">OverComer Identity Declaration</p>
        </div>
      )}

      {/* Quotes Grid */}
      <div className="space-y-4">
        {filteredQuotes.map(quote => (
          <QuoteCard key={quote.id} quote={quote} onRenewMind={handleRenewMind} />
        ))}
      </div>

      {/* Devotional Resources */}
      {(!selectedCategory || selectedCategory === 'STRENGTH_FAITH' || selectedCategory === 'IAM_DECLARATIONS') && (
        <div className="mt-6 space-y-3">
          <h3 className="font-bold text-gray-900 flex items-center gap-2">
            <FileText className="w-5 h-5 text-primary-500" />
            Devotional Reading
          </h3>

          {/* Trust The Process — inline devotional */}
          <TrustTheProcessDevotional />

          {[
            {
              title: 'Seek God With All Your Heart',
              description: 'A deep devotional on pursuing God wholeheartedly in your recovery walk.',
              pdf: '/philosophy/Seek_God_With_All_Your_Heart.pdf'
            },
            {
              title: 'Seek First the Kingdom of God',
              description: 'Study guide on Matthew 6:33 — making God\'s Kingdom your daily priority.',
              pdf: '/philosophy/Seek_1st_The_Kingdom_Of_God.pdf'
            },
            {
              title: 'Tell Your Story: 10 Tips for Sharing Your Testimony',
              description: 'How to share what God has done in your life to help and inspire others.',
              pdf: '/philosophy/Tell_Your_Story__10_Tips_for_Sharing_Your_Testimony_With_Others.pdf'
            }
          ].map((doc, i) => (
            <div key={i} className="bg-white rounded-2xl p-4 shadow-md border border-gray-100 flex items-start gap-3">
              <div className="w-10 h-10 bg-primary-100 rounded-xl flex items-center justify-center flex-shrink-0">
                <FileText className="w-5 h-5 text-primary-500" />
              </div>
              <div className="flex-1">
                <h4 className="font-bold text-gray-900 text-sm leading-snug">{doc.title}</h4>
                <p className="text-xs text-gray-500 mt-1 leading-relaxed">{doc.description}</p>
                <a
                  href={doc.pdf}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="mt-2 inline-flex items-center gap-1.5 text-xs text-primary-600 font-semibold hover:text-primary-700 transition-colors"
                >
                  <Download className="w-3.5 h-3.5" />
                  Open PDF
                </a>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Trusted Voices Library */}
      {(!selectedCategory) && <TrustedVoicesLibrary />}
    </div>
  )
}

function QuoteCard({ quote, onRenewMind }: { quote: InspirationQuote; onRenewMind: (quote: InspirationQuote) => void }) {
  const catInfo = categoryInfo[quote.category]

  return (
    <div className="bg-white rounded-2xl p-5 shadow-md border border-gray-100">
      <div className="flex items-start gap-3">
        <div className={`w-10 h-10 ${catInfo.color} rounded-xl flex items-center justify-center text-white flex-shrink-0`}>
          <Quote className="w-5 h-5" />
        </div>
        <div className="flex-1">
          <p className="text-gray-800 font-medium leading-relaxed italic">
            "{quote.text}"
          </p>
          <p className="text-sm text-gray-500 mt-2 font-semibold">
            — {quote.reference}
          </p>
        </div>
      </div>

      <button
        onClick={() => onRenewMind(quote)}
        className="mt-4 w-full flex items-center justify-center gap-2 text-primary-500 font-semibold text-sm hover:bg-primary-50 py-2 rounded-xl transition-colors"
      >
        <RefreshCw className="w-4 h-4" />
        Renew My Mind
      </button>
    </div>
  )
}

function TrustTheProcessDevotional() {
  const [expanded, setExpanded] = useState(false)

  return (
    <div className="bg-white rounded-2xl shadow-md border border-primary-100 overflow-hidden">
      {/* Header */}
      <div className="bg-gradient-to-br from-primary-500 to-primary-700 p-4 text-white">
        <div className="flex items-start gap-3">
          <div className="w-10 h-10 bg-white/20 rounded-xl flex items-center justify-center flex-shrink-0">
            <BookOpen className="w-5 h-5 text-white" />
          </div>
          <div className="flex-1">
            <h4 className="font-black text-base leading-snug">Trust The Process</h4>
            <p className="text-xs text-white/80 mt-0.5">Proverbs 13:12 · The Story of David</p>
            <p className="text-xs text-white/70 mt-1">Shane Merrill & Micah Cartee</p>
          </div>
        </div>

        {/* Key verse */}
        <div className="mt-3 bg-white/10 rounded-xl p-3">
          <p className="text-sm italic text-white leading-relaxed">
            "Hope deferred makes the heart sick, but a longing fulfilled is a tree of life."
          </p>
          <p className="text-xs text-white/70 mt-1 font-semibold">Proverbs 13:12 (NIV)</p>
        </div>
      </div>

      {/* Preview / Read More */}
      {!expanded ? (
        <div className="p-4">
          <p className="text-sm text-gray-600 leading-relaxed">
            There are times when God puts things in our hearts — dreams, aspirations, goals — and makes us wait. During that waiting, God prepares us for His promise. The question is: <strong className="text-gray-800">Will you trust the process?</strong>
          </p>
          <button
            onClick={() => setExpanded(true)}
            className="mt-3 w-full flex items-center justify-center gap-2 bg-primary-50 hover:bg-primary-100 text-primary-600 font-semibold text-sm py-2.5 rounded-xl transition-colors"
          >
            <ChevronDown className="w-4 h-4" />
            Read Full Devotional
          </button>
        </div>
      ) : (
        <div className="p-4 space-y-5 text-sm text-gray-700 leading-relaxed">

          {/* Section 1 */}
          <div>
            <p>There are times when God puts things in our hearts — dreams, aspirations, goals — and He gives them to us right away. There are other times, and this seems to be the majority, when God puts things in our hearts and then makes us wait.</p>
            <p className="mt-2">Often, we can wonder what God is doing. We can begin to ask, <em>"Did I really hear from God?"</em> Or perhaps the biggest question: <em>"God, what is taking You so long?"</em></p>
            <p className="mt-2">I have found that during those times of waiting, God prepares us for His promise. During our waiting, He teaches us things. He grows our character, our faith, and our ability to do what He has called us to do. God takes us through a process.</p>
          </div>

          {/* Callout */}
          <div className="bg-primary-50 border-l-4 border-primary-400 rounded-r-xl p-3">
            <p className="font-bold text-primary-700 text-base">Will You Trust the Process?</p>
          </div>

          {/* Section 2 — Samuel & David */}
          <div>
            <p className="font-bold text-gray-900">Chosen By God</p>
            <p className="mt-1">In 1 Samuel 16, God came to Samuel and told him to go to Jesse's tribe, where He had chosen one of his sons to be king. When Samuel arrived, he looked at Eliab — the oldest, probably the tallest — and thought, <em>"Surely this is the one."</em></p>
            <p className="mt-2">But God said:</p>
            <div className="my-2 bg-gray-50 rounded-xl p-3 italic text-gray-600">
              "Do not consider his appearance or his height, for I have rejected him. The Lord does not look at the things people look at. People look at the outward appearance, but the <strong>Lord looks at the heart.</strong>" — 1 Samuel 16:7 (NIV)
            </div>
            <p>One by one, Jesse's seven sons passed before Samuel. Each time: No. No. No. Finally Samuel asked, "Are there any more?" Jesse answered, "There is still the youngest — he is tending the sheep." So they sent for David.</p>
            <p className="mt-2">Samuel anointed David — and then the story stops. David went back to watching the sheep.</p>
          </div>

          {/* Callout 2 */}
          <div className="bg-secondary-50 border-l-4 border-secondary-400 rounded-r-xl p-3">
            <p className="font-semibold text-secondary-700">God anointed David long before he became king because God wanted to take David through a <em>process</em> — and the anointing was a reminder of the promise that the process would bring.</p>
          </div>

          {/* Chinese Bamboo */}
          <div>
            <p className="font-bold text-gray-900">Things Have To Change</p>
            <p className="mt-1">A process is a series of actions or steps taken in order to achieve a particular end. God knew that things needed to happen in David's life before he could be king. Likewise, there are things that need to happen in our lives before we can step into what God has for us.</p>
            <p className="mt-2">The Chinese Bamboo Tree is planted as a nut and must be watered and fertilized every single day for five years before it breaks through the ground. All that time, it spreads its roots. Then in the fifth year, it breaks through and grows to nearly ninety feet tall in just six weeks.</p>
            <p className="mt-2 font-medium text-gray-800">We tend to get frustrated when we don't get five-year results immediately. But the process is essential — and everyone must go through it.</p>
            <p className="mt-2">The question is: <strong>will you allow the waiting to develop you, or embitter you?</strong></p>
          </div>

          {/* Callout 3 */}
          <div className="bg-accent-coral/10 border-l-4 border-accent-coral rounded-r-xl p-3">
            <p className="font-bold text-accent-coral">Bitterness is unbelief in the promises of God.</p>
            <p className="mt-1 text-gray-600 text-xs">Faith is what keeps you going until you see God do what He has promised. "I may not have seen it yet — but if God promised it, I am sure I will!"</p>
          </div>

          {/* Anointed vs Appointed */}
          <div>
            <p className="font-bold text-gray-900">Anointed, Not Yet Appointed</p>
            <p className="mt-1">David had the <em>anointing</em> to be king — but not yet the <em>appointment</em>. Sometimes you can have an anointing on your life to do something, but not the appointing.</p>
            <p className="mt-2">The danger is tunnel vision — being so focused on the light at the end of the tunnel that you miss what God is doing in and around you right now. You can miss learning experiences and opportunities because all you can see is the future dream.</p>
          </div>

          {/* Preparation */}
          <div>
            <p className="font-bold text-gray-900">Preparation is Key</p>
            <div className="my-2 bg-primary-50 rounded-xl p-3">
              <p className="font-bold text-primary-700 text-center">Preparation must come before the opportunity.</p>
            </div>
            <p>After his anointing, David's appointment was to watch the sheep. It was there that he developed his ability to play the lyre — and wrote Psalm 19:1. When Saul needed someone to soothe his tormented spirit, a servant said: "I have seen a son of Jesse who knows how to play the lyre. He is a brave man and a warrior... and the Lord is with him." (1 Samuel 16:18)</p>
            <p className="mt-2">Then came Goliath. When Saul said David was too young, David responded: <em>"Your servant used to keep sheep for his father. And when there came a lion, or a bear, and took a lamb from the flock, I went after him and struck him... Your servant has struck down both lions and bears."</em> (1 Samuel 17:34-36)</p>
            <p className="mt-2 font-medium text-gray-800">The preparation had to come before the opportunity — every single time.</p>
          </div>

          {/* Callout 4 */}
          <div className="bg-accent-gold/15 border-l-4 border-accent-gold rounded-r-xl p-3">
            <p className="font-bold text-gray-800">If You Shortcut the Process, You Short-Circuit the Product.</p>
            <p className="mt-1 text-gray-600 text-xs">If David had never mastered the lyre, he never would have been chosen to play for Saul. If he had never killed the lion and the bear, Saul would not have let him face Goliath. Preparation opened every door.</p>
          </div>

          {/* Conclusion */}
          <div>
            <p className="font-bold text-gray-900">You Can Trust the Process</p>
            <p className="mt-1">You may be in a season of waiting. Maybe you have been waiting for a long time and are starting to wonder if God will ever bring about His promise. The fact is — <strong className="text-primary-600">you can trust the process.</strong></p>
            <p className="mt-2">Let your faith be strengthened. Know that God is preparing you today for what He wants to do through you in the future. He's teaching you to trust Him, to be bold in your faith, growing your character, and fine-tuning your gifts.</p>
            <div className="mt-3 bg-primary-500 rounded-xl p-3 text-white text-center">
              <p className="font-black text-base">Trust Him.</p>
              <p className="text-sm text-white/90 mt-0.5">The process is bringing about the promise.</p>
            </div>
          </div>

          <p className="text-xs text-gray-400 text-right">— Shane Merrill & Micah Cartee · James River College</p>

          <button
            onClick={() => setExpanded(false)}
            className="w-full flex items-center justify-center gap-2 bg-gray-100 hover:bg-gray-200 text-gray-600 font-semibold text-sm py-2.5 rounded-xl transition-colors"
          >
            <ChevronUp className="w-4 h-4" />
            Collapse
          </button>
        </div>
      )}
    </div>
  )
}

interface TrustedBook {
  title: string
  author: string
  description: string
  tag: string
  tagColor: string
}

const trustedVoicesData: Array<{ topic: string; color: string; books: TrustedBook[] }> = [
  {
    topic: 'Substance Recovery',
    color: 'bg-accent-teal',
    books: [
      {
        title: 'The Cross and the Switchblade',
        author: 'David Wilkerson',
        description: 'The story that launched Teen Challenge — the gold standard in faith-based recovery ministry.',
        tag: 'Recovery',
        tagColor: 'bg-accent-teal/10 text-accent-teal'
      },
      {
        title: 'Run Baby Run',
        author: 'Nicky Cruz',
        description: 'From gang life to Christ — a raw, powerful testimony that addiction and violence have no hold on a life surrendered to Jesus.',
        tag: 'Testimony',
        tagColor: 'bg-accent-teal/10 text-accent-teal'
      },
      {
        title: 'Crossroads: A Step-by-Step Guide Away from Addiction',
        author: 'Edward T. Welch',
        description: 'A strictly biblical workbook for recovery — addresses behavioral choices and personal accountability head-on.',
        tag: 'Workbook',
        tagColor: 'bg-accent-teal/10 text-accent-teal'
      },
    ],
  },
  {
    topic: 'Mental Health & Faith',
    color: 'bg-primary-400',
    books: [
      {
        title: 'Grace for the Afflicted',
        author: 'Dr. Matthew Stanford',
        description: 'A Christian neuroscientist bridges faith and clinical care — the definitive guide for ministry leaders navigating mental illness.',
        tag: 'Clinical + Faith',
        tagColor: 'bg-primary-100 text-primary-600'
      },
      {
        title: 'Blame It on the Brain?',
        author: 'Edward T. Welch',
        description: 'Helps leaders discern when a struggle is spiritual, behavioral, or requires professional clinical care.',
        tag: 'Discernment',
        tagColor: 'bg-primary-100 text-primary-600'
      },
      {
        title: 'Boundaries: When to Say Yes, How to Say No',
        author: 'Dr. Henry Cloud & Dr. John Townsend',
        description: 'A theological and psychological framework for deep compassion paired with uncompromising accountability.',
        tag: 'Relationships',
        tagColor: 'bg-primary-100 text-primary-600'
      },
    ],
  },
  {
    topic: 'Inner Healing & Transformation',
    color: 'bg-secondary-500',
    books: [
      {
        title: 'Instruments in the Redeemer\'s Hands',
        author: 'Paul David Tripp',
        description: 'How ordinary believers can engage in personal, transformative ministry — speaking truth into each other\'s lives.',
        tag: 'Ministry',
        tagColor: 'bg-secondary-100 text-secondary-600'
      },
      {
        title: 'The Wounded Heart',
        author: 'Dr. Dan B. Allender',
        description: 'A landmark text in Christian trauma care — addresses the deep damage of abuse with immense empathy and theological depth.',
        tag: 'Trauma',
        tagColor: 'bg-secondary-100 text-secondary-600'
      },
      {
        title: 'Connecting',
        author: 'Dr. Larry Crabb',
        description: 'True community as the vehicle for healing — where we are fully known and fully loved, mirroring God\'s heart.',
        tag: 'Community',
        tagColor: 'bg-secondary-100 text-secondary-600'
      },
    ],
  },
  {
    topic: 'Marriage & Relationships',
    color: 'bg-accent-coral',
    books: [
      {
        title: 'Sacred Marriage',
        author: 'Gary Thomas',
        description: 'What if God designed marriage to make us holy more than to make us happy? A cornerstone shift in perspective.',
        tag: 'Marriage',
        tagColor: 'bg-red-50 text-red-500'
      },
      {
        title: 'Love & Respect',
        author: 'Dr. Emerson Eggerichs',
        description: 'Grounded in Ephesians 5:33 — breaks down the communication cycles that destroy marriages with practical, actionable steps.',
        tag: 'Communication',
        tagColor: 'bg-red-50 text-red-500'
      },
      {
        title: 'Vertical Marriage',
        author: 'Dave & Ann Wilson',
        description: 'Your horizontal relationship with your spouse can only be transformed by first fixing your vertical relationship with Christ.',
        tag: 'Marriage',
        tagColor: 'bg-red-50 text-red-500'
      },
    ],
  },
  {
    topic: 'Pastoral & Christian Counseling',
    color: 'bg-accent-gold',
    books: [
      {
        title: 'Christian Counseling: A Comprehensive Guide',
        author: 'Dr. Gary R. Collins',
        description: 'The standard textbook for Christian counselors — covers a vast range of counseling scenarios with proven frameworks.',
        tag: 'Counseling',
        tagColor: 'bg-yellow-50 text-yellow-600'
      },
      {
        title: 'Seeing with New Eyes',
        author: 'Dr. David Powlison',
        description: 'How Scripture diagnoses human motives and brings practical, grace-centered change to daily struggles.',
        tag: 'Scripture',
        tagColor: 'bg-yellow-50 text-yellow-600'
      },
      {
        title: 'Dare to Discipline / The Strong-Willed Child',
        author: 'Dr. James Dobson',
        description: 'The foundational framework for loving, firm parenting rooted in biblical principles — essential for families in recovery.',
        tag: 'Family',
        tagColor: 'bg-yellow-50 text-yellow-600'
      },
    ],
  },
]

export function TrustedVoicesLibrary() {
  const [expandedTopic, setExpandedTopic] = useState<string | null>(null)

  return (
    <div className="mt-8">
      <div className="flex items-center gap-2 mb-4">
        <Users className="w-5 h-5 text-primary-500" />
        <h3 className="font-bold text-gray-900">Trusted Voices Library</h3>
      </div>
      <p className="text-sm text-gray-500 mb-4 leading-relaxed">
        Biblically vetted authors and resources — carefully selected to align with the OverComer worldview.
      </p>

      <div className="space-y-3">
        {trustedVoicesData.map(section => (
          <div key={section.topic} className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <button
              onClick={() => setExpandedTopic(expandedTopic === section.topic ? null : section.topic)}
              className="w-full flex items-center justify-between p-4 text-left"
            >
              <div className="flex items-center gap-3">
                <div className={`w-8 h-8 ${section.color} rounded-lg flex items-center justify-center`}>
                  <BookOpen className="w-4 h-4 text-white" />
                </div>
                <span className="font-bold text-gray-900 text-sm">{section.topic}</span>
                <span className="text-xs text-gray-400">{section.books.length} resources</span>
              </div>
              {expandedTopic === section.topic
                ? <ChevronUp className="w-4 h-4 text-gray-400 flex-shrink-0" />
                : <ChevronDown className="w-4 h-4 text-gray-400 flex-shrink-0" />
              }
            </button>

            {expandedTopic === section.topic && (
              <div className="border-t border-gray-100 divide-y divide-gray-50">
                {section.books.map((book, i) => (
                  <div key={i} className="p-4">
                    <div className="flex items-start justify-between gap-2 mb-1">
                      <h4 className="font-bold text-gray-900 text-sm leading-snug">{book.title}</h4>
                      <span className={`flex-shrink-0 text-xs font-semibold px-2 py-0.5 rounded-full ${book.tagColor}`}>
                        {book.tag}
                      </span>
                    </div>
                    <p className="text-xs font-semibold text-primary-500 mb-1">{book.author}</p>
                    <p className="text-xs text-gray-500 leading-relaxed">{book.description}</p>
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
