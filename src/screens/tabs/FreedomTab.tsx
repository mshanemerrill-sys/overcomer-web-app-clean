import { useState, useEffect, useMemo, useCallback } from 'react'
import { useAppStore } from '../../store/useAppStore'
import { generateVerseOfTheDay } from '../../lib/geminiClient'
import { academyLessons } from '../../lib/data'
import {
  Target, Star, BookOpen, ChevronDown, ChevronUp, Check,
  Wind, MapPin, Phone, ExternalLink, MessageCircle, RefreshCw, Download,
  X, Search, ArrowRight, Medal, Heart, Globe, Brain
} from 'lucide-react'
import type { FocusPath, VerseOfTheDay, AcademyLesson } from '../../lib/types'
import VeteransTab from './VeteransTab'
import ReentryTab from './ReentryTab'
import MentalHealthSupport from './MentalHealthSupport'
import { TrustedVoicesLibrary } from './InspirationTab'

interface FreedomTabProps {
  onNavigateToCompanion: () => void
}

export default function FreedomTab({ onNavigateToCompanion }: FreedomTabProps) {
  const { userPath, freedomGoal, freedomGoals, setFreedomGoal, verseOfTheDay, setVerseOfTheDay } = useAppStore()
  const [showSettings, setShowSettings] = useState(false)
  const [showAcademy, setShowAcademy] = useState(false)
  const [isLoadingVerse, setIsLoadingVerse] = useState(false)
  const [selectedLesson, setSelectedLesson] = useState<AcademyLesson | null>(null)

  const path = userPath || 'SUBSTANCE_RECOVERY'
  const pathGoal = freedomGoals[path] || (path === 'SUBSTANCE_RECOVERY' ? freedomGoal : null)

  const defaultDeclaration = {
    SUBSTANCE_RECOVERY: 'An OverComer has submitted their life wholly to Christ and no longer fights FOR victory over addiction — rather FROM a position of victory. I AM Loved By God. I AM NOT Who Others Say I Am. I AM NOT Who I Used To Be. I AM Who God Says I Am.',
    MENTAL_HEALTH: 'My mind belongs to Christ. God has not given me a spirit of fear, but of power, love, and a sound mind. I cast every anxious thought on Him, for He cares for me. I AM NOT Who I Used To Be — I AM a New Creation.',
    TOUGH_DAY: 'This day does not define me. Greater is He that is in me than he that is in the world. I choose to seek God first today. His grace is sufficient for me.',
    TESTIMONY_VICTORY: 'I am more than a conqueror through Christ who loves me! They overcame him by the blood of the Lamb and by the word of their testimony. My story is not over — God is still writing it!',
    VETERAN_TRANSITION: 'I am commissioned by King Jesus. He is my shield, my deliverer, and my secure fortress in every battle.',
    REENTRY_RESTORATION: 'My past is nailed to the cross, and I am a new creation in Christ. God has a future and a hope for me to lead and thrive in society.'
  }

  const refreshVerse = useCallback(async () => {
    setIsLoadingVerse(true)
    try {
      const verse = await generateVerseOfTheDay()
      setVerseOfTheDay(verse)
    } catch {
      // Fallback is handled in the client
    } finally {
      setIsLoadingVerse(false)
    }
  }, [setVerseOfTheDay])

  useEffect(() => {
    if (!verseOfTheDay) void refreshVerse()
  }, [refreshVerse, verseOfTheDay])

  const daysCount = pathGoal?.startDate
    ? Math.floor((Date.now() - pathGoal.startDate) / (1000 * 60 * 60 * 24))
    : 0

  return (
    <div className="p-4 pb-8 space-y-4">
      {/* Verse of the Day */}
      <VerseCard
        verse={verseOfTheDay}
        isLoading={isLoadingVerse}
        onRefresh={refreshVerse}
      />

      <PathEncouragement path={path} />

      {/* Freedom Day Counter */}
      <FreedomCounterCard
        daysCount={daysCount}
        struggleType={pathGoal?.struggleType || getDefaultStruggle(path)}
        path={path}
        onSettingsClick={() => setShowSettings(true)}
      />

      {/* Personal Declaration / Creed */}
      <DeclarationCard
        declaration={pathGoal?.customDeclaration || defaultDeclaration[path]}
        path={path}
      />

      {/* Bible Affirmations */}
      <AffirmationsCard path={path} />

      {path === 'TESTIMONY_VICTORY' && <TestimonyVictoryBoard onNavigateToCompanion={onNavigateToCompanion} />}

      {/* The Android app shows the OverComer lessons on the recovery path. */}
      {path === 'SUBSTANCE_RECOVERY' && (
        <AcademyCard
          isExpanded={showAcademy}
          onToggle={() => setShowAcademy(!showAcademy)}
          onSelectLesson={setSelectedLesson}
        />
      )}

      {path === 'MENTAL_HEALTH' && (
        <EmbeddedSupportSection title="Vetted Biblical Clinical Counseling" icon={<Brain className="w-5 h-5" />}>
          <MentalHealthSupport />
        </EmbeddedSupportSection>
      )}

      {path === 'VETERAN_TRANSITION' && (
        <EmbeddedSupportSection title="The Next Mission: Veteran Support & Wellness" icon={<Medal className="w-5 h-5" />}>
          <div className="p-4 pb-0"><VeteranCrisisBanner /></div>
          <VeteransTab />
        </EmbeddedSupportSection>
      )}

      {path === 'REENTRY_RESTORATION' && (
        <EmbeddedSupportSection title="Steps to Restoration & Freedom" icon={<RefreshCw className="w-5 h-5" />}>
          <ReentryTab />
        </EmbeddedSupportSection>
      )}

      {/* Support & Church Locator */}
      <SupportLocatorCard />

      {/* Vetted biblical resource library */}
      <div className="bg-white rounded-3xl border border-[#CAC4D0]/60 p-4 shadow-sm">
        <TrustedVoicesLibrary />
      </div>

      {/* The Faith Connection */}
      <FaithConnectionCard />

      {path !== 'TESTIMONY_VICTORY' && (
        <>
          <GroundingSkillsLibrary />

          {/* Calming Breathing Support */}
          <BreathingCard />

          {/* SOS Support Network */}
          <SOSNetworkCard />
        </>
      )}

      {/* Open Companion Button */}
      <button
        onClick={onNavigateToCompanion}
        className="w-full bg-primary-400 hover:bg-primary-500 text-white font-bold py-4 px-6 rounded-2xl shadow-lg transition-all hover:shadow-xl active:scale-98 flex items-center justify-center gap-3"
      >
        <MessageCircle className="w-5 h-5" />
        Talk to OverComer Companion
      </button>

      {/* Settings Modal */}
      {showSettings && (
        <SettingsModal
          path={path}
          startDate={pathGoal?.startDate || Date.now() - (3 * 24 * 60 * 60 * 1000)}
          struggleType={pathGoal?.struggleType || getDefaultStruggle(path)}
          declaration={pathGoal?.customDeclaration || defaultDeclaration[path]}
          onSave={(startDate, struggleType, declaration) => {
            setFreedomGoal({ startDate, struggleType, customDeclaration: declaration })
            setShowSettings(false)
          }}
          onClose={() => setShowSettings(false)}
        />
      )}

      {/* Lesson Detail Modal */}
      {selectedLesson && (
        <LessonModal
          lesson={selectedLesson}
          onClose={() => setSelectedLesson(null)}
        />
      )}
    </div>
  )
}

function getDefaultStruggle(path: FocusPath): string {
  switch (path) {
    case 'MENTAL_HEALTH': return 'Anxiety & Depression'
    case 'TESTIMONY_VICTORY': return 'Victorious Breakthrough'
    case 'TOUGH_DAY': return 'Daily Stress'
    case 'VETERAN_TRANSITION': return 'Veteran Transition & PTSD'
    case 'REENTRY_RESTORATION': return 'Re-entry Reintegration'
    default: return 'Substance Use'
  }
}

function PathEncouragement({ path }: { path: FocusPath }) {
  const encouragement: Record<FocusPath, string> = {
    SUBSTANCE_RECOVERY: 'Remember: You are not defined by your struggle, but by His grace. You are a new creation in Christ. Renew your mind, breathe deeply, and walk in absolute victory today.',
    MENTAL_HEALTH: 'Remember: You are loved, cherished, and chosen by God. Rest in His peace and walk in emotional strength today.',
    TOUGH_DAY: 'Today might feel like an all-around tough day, but God is your present help in times of trouble. Let His supernatural grace carry your load today.',
    VETERAN_TRANSITION: 'Remember: Your identity is anchored in Jesus Christ, who has won the ultimate battle for you. It is alright to ask for help—God is your shield, your fortress, and your deliverer.',
    REENTRY_RESTORATION: 'Remember: Your past is completely forgiven and forgotten by God. Christ has broken every chain of the past and has chosen you to lead with honor and integrity. Walk in His restoration today.',
    TESTIMONY_VICTORY: 'Today is a Testimony and Victory Day! Praise God for His absolute faithfulness, rejoice in His mercy, and walk in the fullness of His triumph today.'
  }

  return (
    <div className="rounded-2xl border border-primary-100 bg-primary-50 px-4 py-3">
      <p className="text-sm leading-relaxed font-semibold text-primary-800">{encouragement[path]}</p>
    </div>
  )
}

function VerseCard({
  verse,
  isLoading,
  onRefresh
}: {
  verse: VerseOfTheDay | null
  isLoading: boolean
  onRefresh: () => void
}) {
  return (
    <div className="bg-gradient-to-br from-primary-500 to-primary-700 rounded-2xl p-5 text-white shadow-lg">
      <div className="flex items-center justify-between mb-3">
        <h3 className="font-bold text-sm uppercase tracking-wide text-white/80">Verse of the Day</h3>
        <button
          onClick={onRefresh}
          disabled={isLoading}
          className="p-1.5 rounded-full bg-white/20 hover:bg-white/30 transition-colors disabled:opacity-50"
        >
          <RefreshCw className={`w-4 h-4 ${isLoading ? 'animate-spin' : ''}`} />
        </button>
      </div>

      {verse ? (
        <>
          <p className="text-lg font-medium leading-relaxed italic">
            "{verse.text}"
          </p>
          <p className="text-sm font-semibold mt-2 text-white/90">
            — {verse.reference}
          </p>
          {verse.reflection && (
            <p className="mt-3 text-sm text-white/80 leading-relaxed">
              {verse.reflection}
            </p>
          )}
        </>
      ) : (
        <p className="text-white/70">Loading...</p>
      )}
    </div>
  )
}

function FreedomCounterCard({
  daysCount,
  struggleType,
  path,
  onSettingsClick
}: {
  daysCount: number
  struggleType: string
  path: FocusPath
  onSettingsClick: () => void
}) {
  const counterLabel = {
    SUBSTANCE_RECOVERY: 'Days of Freedom',
    MENTAL_HEALTH: 'Days of Peace',
    TOUGH_DAY: 'Days of Strength',
    TESTIMONY_VICTORY: 'Days of Victory',
    VETERAN_TRANSITION: 'Days since taking off the uniform & entering civilian strength',
    REENTRY_RESTORATION: 'Days since my release & step into restoration'
  }

  return (
    <div className="bg-white rounded-2xl p-5 shadow-md border border-gray-100">
      <div className="flex items-start justify-between">
        <div>
          <h3 className="font-bold text-gray-900">{counterLabel[path]}</h3>
          <p className="text-sm text-gray-500">{struggleType}</p>
        </div>
        <button
          onClick={onSettingsClick}
          className="p-2 rounded-lg hover:bg-gray-100 transition-colors text-gray-400 hover:text-gray-600"
        >
          <Target className="w-5 h-5" />
        </button>
      </div>

      <div className="mt-4 text-center py-4 bg-gradient-to-br from-primary-50 to-secondary-50 rounded-xl">
        <span className="text-5xl font-black text-primary-500">{daysCount}</span>
        <p className="text-sm text-gray-600 mt-1">days</p>
      </div>
    </div>
  )
}

function DeclarationCard({
  declaration,
  path
}: {
  declaration: string
  path: FocusPath
}) {
  const title = {
    SUBSTANCE_RECOVERY: 'My OverComer Creed',
    MENTAL_HEALTH: 'My Mental Peace Covenant',
    TOUGH_DAY: "Today's Declaration",
    TESTIMONY_VICTORY: 'My Victory Declaration',
    VETERAN_TRANSITION: "My Warrior's Covenant",
    REENTRY_RESTORATION: 'My Restoration Declaration'
  }

  return (
    <div className="bg-gradient-to-br from-secondary-50 to-primary-50 rounded-2xl p-5 shadow-md border border-primary-100">
      <h3 className="font-bold text-primary-600 mb-3">{title[path]}</h3>
      <p className="text-gray-700 leading-relaxed italic">{declaration}</p>
    </div>
  )
}

function AffirmationsCard({ path: _path }: { path: FocusPath }) {
  const affirmations = [
    { text: 'I AM Loved By God — I AM NOT Who Others Say I Am', reference: 'OverComer Declaration' },
    { text: 'I AM a New Creation — the old is gone, the new has come', reference: '2 Corinthians 5:17' },
    { text: 'I AM unquestionably free — the Son has set me free', reference: 'John 8:36 AMP' },
    { text: 'I AM more than a conqueror through Him who loved me', reference: 'Romans 8:37' },
    { text: 'I AM victorious — God gives me the victory through Christ', reference: '1 Corinthians 15:57' },
    { text: 'I AM chosen, a royal priesthood, God\'s special possession', reference: '1 Peter 2:9' },
    { text: 'I AM held together by Christ — the Cross is in my very cells', reference: 'Colossians 1:17' }
  ]

  return (
    <div className="bg-white rounded-2xl p-5 shadow-md border border-gray-100">
      <h3 className="font-bold text-gray-900 mb-3 flex items-center gap-2">
        <Star className="w-5 h-5 text-accent-gold" />
        Bible Affirmations
      </h3>
      <div className="space-y-2">
        {affirmations.map((aff, i) => (
          <div key={i} className="flex items-center gap-2 text-sm">
            <Check className="w-4 h-4 text-accent-teal flex-shrink-0" />
            <span className="text-gray-700">{aff.text}</span>
            <span className="text-xs text-gray-400">({aff.reference})</span>
          </div>
        ))}
      </div>
    </div>
  )
}

const stepWorksheets: Record<number, { pdf: string; notes?: string }> = {
  1: { pdf: '/philosophy/Step_1_Worksheet.pdf' },
  2: { pdf: '/philosophy/Step_2_Worksheet.pdf', notes: '/philosophy/Step_2_Notes.pdf' },
  3: { pdf: '/philosophy/Step_3_Worksheet.pdf' },
  4: { pdf: '/philosophy/Step_4_Worksheet.pdf' },
  5: { pdf: '/philosophy/Step_5_worksheet.pdf' },
  6: { pdf: '/philosophy/Step_6_Worksheet.pdf' },
  7: { pdf: '/philosophy/Step_7_Worksheet.pdf' }
}

function AcademyCard({
  isExpanded,
  onToggle,
  onSelectLesson
}: {
  isExpanded: boolean
  onToggle: () => void
  onSelectLesson: (lesson: AcademyLesson) => void
}) {
  const { completedLessons } = useAppStore()
  const completed = completedLessons.length
  const total = academyLessons.length

  const groupedLessons = academyLessons.reduce((acc, lesson) => {
    const key = lesson.stepNumber
    if (!acc[key]) acc[key] = []
    acc[key].push(lesson)
    return acc
  }, {} as Record<number, typeof academyLessons>)

  return (
    <div className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
      <button
        onClick={onToggle}
        className="w-full flex items-center justify-between p-5 hover:bg-gray-50 transition-colors"
      >
        <div className="flex items-center gap-3">
          <BookOpen className="w-6 h-6 text-primary-500" />
          <div className="text-left">
            <h3 className="font-bold text-gray-900">OverComer Obedience Academy</h3>
            <p className="text-sm text-gray-500">{completed} / {total} Completed</p>
          </div>
        </div>
        {isExpanded ? (
          <ChevronUp className="w-5 h-5 text-gray-400" />
        ) : (
          <ChevronDown className="w-5 h-5 text-gray-400" />
        )}
      </button>

      {isExpanded && (
        <div className="border-t border-gray-100 p-4 space-y-4 max-h-[32rem] overflow-y-auto">
          {Object.entries(groupedLessons).map(([stepNum, lessons]) => {
            const stepN = Number(stepNum)
            const worksheet = stepWorksheets[stepN]
            return (
              <div key={stepNum}>
                <div className="flex items-center justify-between mb-2">
                  <h4 className="font-bold text-sm text-primary-600">
                    Step {stepNum}: {lessons[0].stepTitle}
                  </h4>
                  {worksheet && (
                    <div className="flex items-center gap-1.5">
                      <a
                        href={worksheet.pdf}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="flex items-center gap-1 text-xs bg-primary-50 hover:bg-primary-100 text-primary-600 font-semibold px-2 py-1 rounded-lg transition-colors"
                        onClick={e => e.stopPropagation()}
                      >
                        <Download className="w-3 h-3" />
                        Worksheet
                      </a>
                      {worksheet.notes && (
                        <a
                          href={worksheet.notes}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="flex items-center gap-1 text-xs bg-accent-teal/10 hover:bg-accent-teal/20 text-accent-teal font-semibold px-2 py-1 rounded-lg transition-colors"
                          onClick={e => e.stopPropagation()}
                        >
                          <Download className="w-3 h-3" />
                          Notes
                        </a>
                      )}
                    </div>
                  )}
                </div>
                <div className="space-y-2">
                  {lessons.map(lesson => (
                    <button
                      key={lesson.id}
                      onClick={() => onSelectLesson(lesson)}
                      className={`w-full flex items-center gap-3 p-3 rounded-xl text-left transition-colors ${
                        completedLessons.includes(lesson.id)
                          ? 'bg-accent-teal/10 border border-accent-teal/20'
                          : 'bg-gray-50 hover:bg-primary-50'
                      }`}
                    >
                      <div className={`w-6 h-6 rounded-full flex items-center justify-center flex-shrink-0 ${
                        completedLessons.includes(lesson.id)
                          ? 'bg-accent-teal text-white'
                          : 'bg-gray-200 text-gray-400'
                      }`}>
                        {completedLessons.includes(lesson.id) ? (
                          <Check className="w-4 h-4" />
                        ) : (
                          <span className="text-xs font-bold">{lesson.id}</span>
                        )}
                      </div>
                      <span className="flex-1 text-sm font-medium text-gray-700">
                        {lesson.title}
                      </span>
                      <ArrowRight className="w-4 h-4 text-gray-400 flex-shrink-0" />
                    </button>
                  ))}
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}

type LocatorCategory = 'recovery' | 'groups' | 'churches'

function SupportLocatorCard() {
  const [zip, setZip] = useState('')
  const [submitted, setSubmitted] = useState('')
  const [category, setCategory] = useState<LocatorCategory>('recovery')

  const handleSearch = () => {
    const cleaned = zip.trim()
    if (cleaned.length >= 3) setSubmitted(cleaned)
  }

  const enc = (s: string) => encodeURIComponent(s)

  type LinkItem = {
    name: string
    desc: string
    website?: string
    mapUrl?: string
    phone?: string
  }

  const categoryContent: Record<LocatorCategory, LinkItem[]> = {
    recovery: [
      {
        name: 'Celebrate Recovery',
        desc: 'Christ-centered recovery groups nationwide',
        website: 'https://www.celebraterecovery.com/crgroups',
        mapUrl: `https://www.google.com/maps/search/?api=1&query=Celebrate+Recovery+near+${enc(submitted)}`
      },
      {
        name: 'Teen Challenge',
        desc: 'Faith-based addiction recovery & discipleship',
        website: 'https://teenchallengeusa.org/find-a-center',
        mapUrl: `https://www.google.com/maps/search/?api=1&query=Teen+Challenge+near+${enc(submitted)}`
      },
      {
        name: 'SAMHSA Treatment Locator',
        desc: 'Free, confidential substance use treatment finder',
        website: `https://findtreatment.gov/?location=${enc(submitted)}`
      },
      {
        name: 'Salvation Army Recovery',
        desc: 'Faith-based rehabilitation & shelter programs',
        website: 'https://www.salvationarmyusa.org/usn/locate-a-center/',
        mapUrl: `https://www.google.com/maps/search/?api=1&query=Salvation+Army+near+${enc(submitted)}`
      }
    ],
    groups: [
      {
        name: 'Christian Support Groups',
        desc: 'Bible-based peer support & fellowship near you',
        mapUrl: `https://www.google.com/maps/search/?api=1&query=Christian+support+group+near+${enc(submitted)}`
      },
      {
        name: 'Celebrate Recovery Groups',
        desc: 'Hurt, habit & hang-up recovery groups',
        website: 'https://www.celebraterecovery.com/crgroups',
        mapUrl: `https://www.google.com/maps/search/?api=1&query=Celebrate+Recovery+near+${enc(submitted)}`
      },
      {
        name: 'Teen Challenge Outreach',
        desc: 'Christ-centered support groups & counseling',
        website: 'https://teenchallengeusa.org/find-a-center',
        mapUrl: `https://www.google.com/maps/search/?api=1&query=Teen+Challenge+near+${enc(submitted)}`
      },
      {
        name: 'SAMHSA Helpline',
        desc: 'Free, confidential mental health & substance use support',
        website: 'https://www.samhsa.gov/find-help/national-helpline',
        phone: '1-800-662-4357'
      }
    ],
    churches: [
      {
        name: 'Assemblies of God Churches',
        desc: 'Spirit-filled, Bible-believing congregations near you',
        website: `https://ag.org/churches/find-a-church?zip=${enc(submitted)}`,
        mapUrl: `https://www.google.com/maps/search/?api=1&query=Assemblies+of+God+church+near+${enc(submitted)}`
      },
      {
        name: 'Church of God Congregations',
        desc: 'Pentecostal, Christ-centered churches near you',
        website: `https://www.churchofgod.org/find-a-church/?zip=${enc(submitted)}`,
        mapUrl: `https://www.google.com/maps/search/?api=1&query=Church+of+God+near+${enc(submitted)}`
      },
      {
        name: 'IPHC — Pentecostal Holiness Churches',
        desc: 'International Pentecostal Holiness congregations',
        website: 'https://iphc.org/find-a-church/',
        mapUrl: `https://www.google.com/maps/search/?api=1&query=Pentecostal+Holiness+church+near+${enc(submitted)}`
      },
      {
        name: 'Bible-Believing Churches',
        desc: 'Non-denominational & evangelical churches near you',
        mapUrl: `https://www.google.com/maps/search/?api=1&query=evangelical+Christian+church+near+${enc(submitted)}`
      }
    ]
  }

  const categories: { id: LocatorCategory; label: string; icon: string }[] = [
    { id: 'recovery', label: 'Recovery Groups', icon: '🙏' },
    { id: 'groups', label: 'Support Groups', icon: '🤝' },
    { id: 'churches', label: 'Churches', icon: '✝️' }
  ]

  return (
    <div className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
      <div className="p-5 pb-4">
        <h3 className="font-bold text-gray-900 mb-1 flex items-center gap-2">
          <MapPin className="w-5 h-5 text-primary-500" />
          Find Support Near You
        </h3>
        <p className="text-xs text-gray-500 mb-4">
          Choose what you're looking for, then enter your zip code or city.
        </p>

        {/* Category picker — always visible */}
        <div className="flex gap-1.5 mb-4">
          {categories.map(cat => (
            <button
              key={cat.id}
              onClick={() => { setCategory(cat.id); setSubmitted('') }}
              className={`flex-1 flex flex-col items-center gap-1 py-2.5 px-1 rounded-xl border text-xs font-bold transition-all ${
                category === cat.id
                  ? 'bg-primary-500 border-primary-500 text-white shadow-sm'
                  : 'bg-gray-50 border-gray-200 text-gray-600 hover:bg-primary-50 hover:border-primary-200'
              }`}
            >
              <span className="text-base leading-none">{cat.icon}</span>
              <span className="leading-tight text-center">{cat.label}</span>
            </button>
          ))}
        </div>

        {/* Search input */}
        <div className="flex gap-2">
          <input
            type="text"
            value={zip}
            onChange={e => setZip(e.target.value.slice(0, 30))}
            onKeyDown={e => e.key === 'Enter' && handleSearch()}
            placeholder="Zip code or city name..."
            className="flex-1 input-field"
          />
          <button
            onClick={handleSearch}
            disabled={zip.trim().length < 3}
            className="bg-primary-500 hover:bg-primary-600 disabled:bg-gray-200 text-white font-semibold px-4 py-2.5 rounded-xl transition-colors flex items-center gap-1.5"
          >
            <Search className="w-4 h-4" />
            Find
          </button>
        </div>
      </div>

      {/* Results */}
      {submitted && (
        <div className="px-5 pb-5 space-y-2.5 border-t border-gray-50 pt-4">
          <p className="text-xs text-gray-400 font-semibold">
            {categories.find(c => c.id === category)?.label} near{' '}
            <span className="text-gray-700">"{submitted}"</span>
          </p>
          {categoryContent[category].map((item, i) => (
            <div key={i} className="bg-gray-50 border border-gray-100 rounded-xl p-3">
              <p className="font-bold text-gray-800 text-sm">{item.name}</p>
              <p className="text-xs text-gray-500 mt-0.5 mb-2.5">{item.desc}</p>
              <div className="flex gap-2 flex-wrap">
                {item.mapUrl && (
                  <a
                    href={item.mapUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex items-center gap-1.5 bg-white border border-gray-200 text-gray-700 font-semibold text-xs py-1.5 px-3 rounded-lg hover:bg-gray-50 transition-colors"
                  >
                    <MapPin className="w-3 h-3" />
                    Map Search
                  </a>
                )}
                {item.website && (
                  <a
                    href={item.website}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex items-center gap-1.5 bg-white border border-gray-200 text-gray-700 font-semibold text-xs py-1.5 px-3 rounded-lg hover:bg-gray-50 transition-colors"
                  >
                    <ExternalLink className="w-3 h-3" />
                    Website
                  </a>
                )}
                {item.phone && (
                  <a
                    href={`tel:${item.phone.replace(/\D/g, '')}`}
                    className="flex items-center gap-1.5 bg-white border border-gray-200 text-gray-700 font-semibold text-xs py-1.5 px-3 rounded-lg hover:bg-gray-50 transition-colors"
                  >
                    <Phone className="w-3 h-3" />
                    {item.phone}
                  </a>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pre-search defaults */}
      {!submitted && (
        <div className="px-5 pb-5 space-y-2 border-t border-gray-50 pt-3">
          <a
            href="https://www.celebraterecovery.com/crgroups"
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center justify-between p-3 bg-primary-50 rounded-xl hover:bg-primary-100 transition-colors"
          >
            <div>
              <p className="font-semibold text-primary-700 text-sm">Celebrate Recovery Directory</p>
              <p className="text-xs text-primary-600">Find a CR group anywhere in the US</p>
            </div>
            <ExternalLink className="w-4 h-4 text-primary-500 flex-shrink-0" />
          </a>
          <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-xl">
            <Phone className="w-4 h-4 text-primary-400 flex-shrink-0" />
            <div>
              <p className="font-semibold text-gray-700 text-sm">SAMHSA Helpline</p>
              <a href="tel:18006624357" className="text-xs text-primary-600 font-bold">1-800-662-4357</a>
              <span className="text-xs text-gray-400"> · Free · 24/7 · Confidential</span>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

/* ─────────────────────────────────────────────────────────────────────── */
/* Lesson content renderer — converts the markdown-ish content to JSX     */
/* ─────────────────────────────────────────────────────────────────────── */
function renderLessonContent(content: string) {
  return content.split('\n\n').map((block, i) => {
    if (block.startsWith('**') && block.endsWith('**') && !block.slice(2).includes('**')) {
      return <h3 key={i} className="font-bold text-primary-700 text-base mt-4 mb-1">{block.replace(/\*\*/g, '')}</h3>
    }
    // Bold headings inside a paragraph
    const parts = block.split(/(\*\*[^*]+\*\*)/)
    return (
      <p key={i} className="text-gray-700 text-sm leading-relaxed">
        {parts.map((part, j) =>
          part.startsWith('**') && part.endsWith('**')
            ? <strong key={j} className="text-gray-900">{part.replace(/\*\*/g, '')}</strong>
            : part
        )}
      </p>
    )
  })
}

function LessonModal({
  lesson,
  onClose
}: {
  lesson: AcademyLesson
  onClose: () => void
}) {
  const { completedLessons, toggleLessonComplete } = useAppStore()
  const isCompleted = completedLessons.includes(lesson.id)

  const worksheetPdf: Record<number, string> = {
    1: '/philosophy/Step_1_Worksheet.pdf',
    2: '/philosophy/Step_2_Worksheet.pdf',
    4: '/philosophy/Step_4_Worksheet.pdf',
    5: '/philosophy/Step_5_worksheet.pdf',
    6: '/philosophy/Step_6_Worksheet.pdf',
    7: '/philosophy/Step_7_Worksheet.pdf',
  }
  const shameDoc = lesson.id === 10 ? '/philosophy/Lessen_Shame_and_Fear_by_Admitting_Your_Wrongs.pdf' : null
  const worksheet = worksheetPdf[lesson.stepNumber]

  return (
    <div className="fixed inset-0 bg-black/60 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 animate-fade-in">
      <div className="bg-white rounded-t-3xl sm:rounded-3xl w-full max-w-lg max-h-[90vh] flex flex-col shadow-2xl animate-slide-up">
        {/* Header */}
        <div className="flex items-start justify-between p-5 border-b border-gray-100 flex-shrink-0">
          <div className="flex-1 pr-3">
            <span className="text-xs font-bold text-primary-500 uppercase tracking-wide">
              Step {lesson.stepNumber} · {lesson.stepTitle}
            </span>
            <h2 className="font-bold text-gray-900 text-base mt-0.5 leading-snug">{lesson.title}</h2>
          </div>
          <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-xl flex-shrink-0">
            <X className="w-5 h-5 text-gray-400" />
          </button>
        </div>

        {/* Scrollable content */}
        <div className="flex-1 overflow-y-auto p-5 space-y-2">
          {renderLessonContent(lesson.content)}

          {/* Reflection Questions */}
          {lesson.reflectionQuestions.length > 0 && (
            <div className="mt-5 bg-primary-50 rounded-2xl p-4 space-y-3">
              <h4 className="font-bold text-primary-700 text-sm">Reflection Questions</h4>
              {lesson.reflectionQuestions.map((q, i) => (
                <div key={i} className="flex items-start gap-2">
                  <span className="w-5 h-5 bg-primary-500 text-white rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 mt-0.5">
                    {i + 1}
                  </span>
                  <p className="text-sm text-gray-700 leading-relaxed">{q}</p>
                </div>
              ))}
            </div>
          )}

          {/* Document downloads */}
          {(worksheet || shameDoc) && (
            <div className="mt-4 space-y-2">
              <p className="text-xs font-bold text-gray-400 uppercase tracking-wide">Downloads</p>
              {worksheet && (
                <a
                  href={worksheet}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex items-center gap-3 p-3 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors"
                >
                  <Download className="w-4 h-4 text-primary-500 flex-shrink-0" />
                  <span className="text-sm font-semibold text-gray-700">Step {lesson.stepNumber} Worksheet (PDF)</span>
                  <ExternalLink className="w-3.5 h-3.5 text-gray-400 ml-auto flex-shrink-0" />
                </a>
              )}
              {shameDoc && (
                <a
                  href={shameDoc}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex items-center gap-3 p-3 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors"
                >
                  <Download className="w-4 h-4 text-primary-500 flex-shrink-0" />
                  <span className="text-sm font-semibold text-gray-700">Lessen Shame by Admitting Your Wrongs (PDF)</span>
                  <ExternalLink className="w-3.5 h-3.5 text-gray-400 ml-auto flex-shrink-0" />
                </a>
              )}
            </div>
          )}
        </div>

        {/* Footer actions */}
        <div className="p-5 border-t border-gray-100 flex gap-3 flex-shrink-0">
          <button
            onClick={onClose}
            className="flex-1 btn-outline py-3"
          >
            Close
          </button>
          {!isCompleted && (
            <button
              onClick={() => {
                toggleLessonComplete(lesson.id)
                onClose()
              }}
              className="flex-1 btn-primary py-3 flex items-center justify-center gap-2"
            >
              <Check className="w-4 h-4" />
              Mark Complete
            </button>
          )}
          {isCompleted && (
            <div className="flex-1 flex items-center justify-center gap-2 bg-accent-teal/10 text-accent-teal font-bold py-3 rounded-xl text-sm">
              <Check className="w-4 h-4" />
              Completed
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

function EmbeddedSupportSection({ title, icon, children }: {
  title: string
  icon: React.ReactNode
  children: React.ReactNode
}) {
  const [expanded, setExpanded] = useState(false)
  return (
    <section className="bg-white rounded-3xl border border-[#CAC4D0]/60 shadow-sm overflow-hidden">
      <button onClick={() => setExpanded(value => !value)} className="w-full p-5 flex items-center gap-3 text-left">
        <span className="w-10 h-10 rounded-full bg-primary-100 text-primary-600 flex items-center justify-center flex-shrink-0">{icon}</span>
        <span className="flex-1">
          <span className="block font-extrabold text-[#1D1B20]">{title}</span>
          <span className="block text-xs text-[#49454F] mt-0.5">Biblical guidance, practical tools, and trusted resources</span>
        </span>
        {expanded ? <ChevronUp className="w-5 h-5 text-gray-400" /> : <ChevronDown className="w-5 h-5 text-gray-400" />}
      </button>
      {expanded && <div className="border-t border-gray-100 bg-[#FEF7FF]">{children}</div>}
    </section>
  )
}

function TestimonyVictoryBoard({ onNavigateToCompanion }: { onNavigateToCompanion: () => void }) {
  const shared = useAppStore(state => state.victoryLogs.filter(log => log.type === 'TESTIMONY' && log.isShared))

  return (
    <section className="rounded-3xl border-[1.5px] border-[#FFA000] bg-[#FFF8E1] p-5 shadow-sm">
      <div className="flex items-center gap-3">
        <span className="w-11 h-11 rounded-full bg-[#FFA000]/15 flex items-center justify-center"><Star className="w-6 h-6 text-[#FFA000]" /></span>
        <div>
          <h3 className="font-extrabold text-[#E65100]">Testimony & Victory Board</h3>
          <p className="text-xs text-amber-800">Celebrate what God has done through other OverComers.</p>
        </div>
      </div>
      {shared.length === 0 ? (
        <div className="mt-4 rounded-2xl bg-white/70 p-4 text-center">
          <p className="text-sm font-semibold text-amber-900">No testimonies have been shared on this device yet.</p>
          <p className="text-xs text-amber-700 mt-1">Open Logs, add a Testimony entry, and choose “Add to Testimony & Victory Board.”</p>
        </div>
      ) : (
        <div className="mt-4 space-y-3">
          {shared.slice(0, 8).map(log => (
            <article key={log.id} className="rounded-2xl bg-white p-4 border border-amber-100">
              <p className="text-sm text-gray-800 leading-relaxed whitespace-pre-wrap">“{log.notes}”</p>
              <p className="text-xs font-bold text-[#E65100] mt-2">— {log.authorName || 'An OverComer'}</p>
            </article>
          ))}
        </div>
      )}
      <button onClick={onNavigateToCompanion} className="mt-4 w-full rounded-xl bg-[#FFA000] hover:bg-[#F57C00] text-white py-3 font-bold flex items-center justify-center gap-2">
        <MessageCircle className="w-4 h-4" /> Share Your Victory with the Companion
      </button>
    </section>
  )
}

interface GroundingSkill {
  name: string
  motto: string
  intensity: 'Gentle' | 'Strong' | 'Immediate'
  description: string
  steps: string[]
  verse: string
  reference: string
}

const groundingSkills: GroundingSkill[] = [
  {
    name: 'Pause, Breathe, Observe, Choose', motto: 'Create space before the next decision.', intensity: 'Immediate',
    description: 'Use this when an urge, panic, anger, or racing thought tries to take control.',
    steps: ['Stop moving for one safe moment.', 'Take one slow breath in and a longer breath out.', 'Notice what is happening in your body and thoughts without agreeing with every thought.', 'Choose the next faithful, safe action.'],
    verse: 'Be still, and know that I am God.', reference: 'Psalm 46:10'
  },
  {
    name: 'Cold-Water Reset', motto: 'Help your body slow down quickly.', intensity: 'Immediate',
    description: 'A brief cool-temperature reset can interrupt escalating physical distress.',
    steps: ['Splash cool—not dangerously cold—water on your face.', 'Hold a cool cloth over your cheeks for 20–30 seconds.', 'Breathe slowly while your body settles.', 'Avoid extreme cold if you have a heart or circulation condition.'],
    verse: 'He leads me beside quiet waters, he refreshes my soul.', reference: 'Psalm 23:2–3'
  },
  {
    name: 'Five-Senses Grounding', motto: 'Return your attention to the present.', intensity: 'Strong',
    description: 'Use your senses to step out of a flashback, craving, or spiraling thought.',
    steps: ['Name 5 things you can see.', 'Name 4 things you can feel.', 'Name 3 things you can hear.', 'Name 2 things you can smell.', 'Name 1 thing you can taste or are thankful for.'],
    verse: 'This is the day the Lord has made; we will rejoice and be glad in it.', reference: 'Psalm 118:24'
  },
  {
    name: 'Ride the Urge', motto: 'An urge rises, crests, and passes.', intensity: 'Strong',
    description: 'Observe an urge like a wave instead of treating it as a command.',
    steps: ['Rate the urge from 1 to 10.', 'Notice where you feel it physically.', 'Breathe and watch the intensity change for 90 seconds.', 'Call support and move toward a safe place while the wave passes.'],
    verse: 'God is faithful; he will also provide a way out so that you can endure it.', reference: '1 Corinthians 10:13'
  },
  {
    name: 'Name the Truth', motto: 'Replace the loud lie with God’s truth.', intensity: 'Gentle',
    description: 'Separate what you feel from what is eternally true.',
    steps: ['Write the thought exactly as it appeared.', 'Ask: Is this completely true, or is fear speaking?', 'Write one balanced, truthful statement.', 'Anchor that truth to a specific Scripture.'],
    verse: 'You will know the truth, and the truth will set you free.', reference: 'John 8:32'
  },
  {
    name: 'Opposite Faithful Action', motto: 'Move in the direction of freedom.', intensity: 'Strong',
    description: 'When an emotion pushes you toward an unsafe action, choose a safe action in the opposite direction.',
    steps: ['Name what the emotion is urging you to do.', 'Check whether that action is safe and consistent with your values.', 'Choose one small opposite action.', 'Ask a trusted person to stay with you while you do it.'],
    verse: 'Do not be overcome by evil, but overcome evil with good.', reference: 'Romans 12:21'
  }
]

function GroundingSkillsLibrary() {
  const [expanded, setExpanded] = useState(false)
  const [query, setQuery] = useState('')
  const [intensity, setIntensity] = useState<'All' | GroundingSkill['intensity']>('All')
  const [openSkill, setOpenSkill] = useState<string | null>(null)
  const visible = useMemo(() => groundingSkills.filter(skill => {
    const matchesText = `${skill.name} ${skill.motto} ${skill.description}`.toLowerCase().includes(query.toLowerCase())
    return matchesText && (intensity === 'All' || skill.intensity === intensity)
  }), [query, intensity])

  return (
    <section className="bg-white rounded-3xl border border-[#CAC4D0]/60 shadow-sm overflow-hidden">
      <button onClick={() => setExpanded(value => !value)} className="w-full p-5 flex items-center gap-3 text-left">
        <span className="w-11 h-11 rounded-full bg-teal-50 text-teal-700 flex items-center justify-center"><Target className="w-6 h-6" /></span>
        <span className="flex-1"><span className="block font-extrabold text-[#1D1B20]">Calming Distress Grounding Library</span><span className="block text-xs text-[#49454F] mt-0.5">Interactive skills for urges and intense feelings</span></span>
        {expanded ? <ChevronUp className="w-5 h-5 text-gray-400" /> : <ChevronDown className="w-5 h-5 text-gray-400" />}
      </button>
      {expanded && (
        <div className="border-t border-gray-100 p-4 space-y-3">
          <div className="relative"><Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" /><input value={query} onChange={event => setQuery(event.target.value)} className="input-field pl-9" placeholder="Search grounding skills..." /></div>
          <div className="flex gap-2 overflow-x-auto">
            {(['All', 'Gentle', 'Strong', 'Immediate'] as const).map(value => <button key={value} onClick={() => setIntensity(value)} className={`rounded-full px-3 py-1.5 text-xs font-bold whitespace-nowrap ${intensity === value ? 'bg-teal-700 text-white' : 'bg-gray-100 text-gray-600'}`}>{value}</button>)}
          </div>
          {visible.map(skill => {
            const open = openSkill === skill.name
            return (
              <article key={skill.name} className="rounded-2xl border border-gray-100 bg-[#FEF7FF] overflow-hidden">
                <button onClick={() => setOpenSkill(open ? null : skill.name)} className="w-full p-4 flex items-start gap-3 text-left">
                  <span className="w-9 h-9 rounded-full bg-teal-100 text-teal-700 flex items-center justify-center flex-shrink-0"><Wind className="w-4 h-4" /></span>
                  <span className="flex-1"><span className="block text-sm font-extrabold text-[#1D1B20]">{skill.name}</span><span className="block text-xs text-[#49454F] mt-1">{skill.motto}</span></span>
                  <span className="text-[10px] font-bold rounded-full bg-white px-2 py-1 text-teal-700">{skill.intensity}</span>
                </button>
                {open && <div className="px-4 pb-4 text-sm text-[#49454F] space-y-3"><p>{skill.description}</p><ol className="space-y-2">{skill.steps.map((step, index) => <li key={step} className="flex gap-2"><span className="w-5 h-5 rounded-full bg-teal-700 text-white text-[10px] font-bold flex items-center justify-center flex-shrink-0">{index + 1}</span><span>{step}</span></li>)}</ol><blockquote className="rounded-xl bg-white p-3 text-primary-700 italic">“{skill.verse}”<br /><strong className="not-italic text-xs">— {skill.reference}</strong></blockquote></div>}
              </article>
            )
          })}
          <p className="text-[11px] text-center text-gray-400 px-3">These calming steps draw from established distress-tolerance practices and are presented through OverComer’s biblical framework.</p>
        </div>
      )}
    </section>
  )
}

function BreathingCard() {
  const [active, setActive] = useState(false)
  const [seconds, setSeconds] = useState(4)
  const [phaseIndex, setPhaseIndex] = useState(0)
  const phases = useMemo(() => [
    { name: 'Breathe in', seconds: 4, scale: 'scale-110', color: 'bg-teal-500' },
    { name: 'Hold gently', seconds: 2, scale: 'scale-110', color: 'bg-primary-400' },
    { name: 'Breathe out', seconds: 6, scale: 'scale-75', color: 'bg-primary-600' },
    { name: 'Rest', seconds: 2, scale: 'scale-75', color: 'bg-gray-400' }
  ], [])
  const phase = phases[phaseIndex]

  useEffect(() => {
    if (!active) return
    const timer = window.setTimeout(() => {
      if (seconds > 1) setSeconds(seconds - 1)
      else {
        const next = (phaseIndex + 1) % phases.length
        setPhaseIndex(next)
        setSeconds(phases[next].seconds)
      }
    }, 1000)
    return () => window.clearTimeout(timer)
  }, [active, seconds, phaseIndex, phases])

  return (
    <div className="bg-gradient-to-br from-accent-teal/10 to-primary-50 rounded-2xl p-5 shadow-md border border-accent-teal/20">
      <h3 className="font-bold text-gray-900 mb-2 flex items-center gap-2">
        <Wind className="w-5 h-5 text-accent-teal" />
        Calming Breathing Support
      </h3>
      <p className="text-sm text-gray-600 mb-3">
        Practice paced breathing to reduce stress and anxiety.
      </p>
      {active && (
        <div className="py-5 flex flex-col items-center">
          <div className={`w-28 h-28 ${phase.color} ${phase.scale} rounded-full transition-all duration-1000 text-white flex flex-col items-center justify-center shadow-lg`}>
            <span className="font-extrabold">{phase.name}</span><span className="text-3xl font-black">{seconds}</span>
          </div>
          <p className="mt-5 text-xs text-gray-500">Let the longer exhale tell your body it is safe to settle.</p>
        </div>
      )}
      <button onClick={() => { setActive(value => !value); setPhaseIndex(0); setSeconds(4) }} className="w-full bg-accent-teal hover:bg-teal-700 text-white font-semibold py-3 px-4 rounded-xl transition-colors">
        {active ? 'Stop Breathing Exercise' : 'Start Breathing Exercise'}
      </button>
    </div>
  )
}

function SOSNetworkCard() {
  return (
    <div className="bg-red-50 rounded-2xl p-5 shadow-md border border-red-100">
      <h3 className="font-bold text-red-700 mb-2 flex items-center gap-2">
        <Phone className="w-5 h-5" />
        SOS Support Network
      </h3>
      <p className="text-sm text-red-600">
        If you're in crisis, reach out to your support network or call emergency services immediately.
      </p>
    </div>
  )
}

function SettingsModal({
  path: _path,
  startDate,
  struggleType,
  declaration,
  onSave,
  onClose
}: {
  path: FocusPath
  startDate: number
  struggleType: string
  declaration: string
  onSave: (startDate: number, struggleType: string, declaration: string) => void
  onClose: () => void
}) {
  const [dateStr, setDateStr] = useState(new Date(startDate).toISOString().split('T')[0])
  const [struggle, setStruggle] = useState(struggleType)
  const [dec, setDec] = useState(declaration)

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl w-full max-w-md max-h-[90vh] overflow-y-auto animate-slide-up">
        <div className="p-5 border-b border-gray-100">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-bold text-gray-900">Freedom Settings</h2>
            <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-lg">
              ✕
            </button>
          </div>
        </div>

        <div className="p-5 space-y-4">
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">Start Date</label>
            <input
              type="date"
              value={dateStr}
              onChange={(e) => setDateStr(e.target.value)}
              className="input-field"
              placeholder="YYYY"  // Removed unused label prop
            />
          </div>

          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">Struggle Type</label>
            <select
              value={struggle}
              onChange={(e) => setStruggle(e.target.value)}
              className="input-field"
            >
              <option value="Substance Use">Substance Use</option>
              <option value="Anxiety & Depression">Anxiety & Depression</option>
              <option value="Fear">Fear</option>
              <option value="Other">Other</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">My Declaration</label>
            <textarea
              value={dec}
              onChange={(e) => setDec(e.target.value)}
              rows={4}
              className="input-field resize-none"
              placeholder="Write your personal declaration..."
            />
          </div>
        </div>

        <div className="p-5 border-t border-gray-100 flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 btn-outline"
          >
            Cancel
          </button>
          <button
            onClick={() => {
              onSave(
                new Date(dateStr).getTime(),
                struggle,
                dec
              )
            }}
            className="flex-1 btn-primary"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  )
}

function VeteranCrisisBanner() {
  return (
    <div className="bg-gradient-to-br from-green-700 to-teal-700 rounded-2xl p-4 text-white">
      <div className="flex items-center gap-3 mb-3">
        <div className="w-10 h-10 bg-white/20 rounded-xl flex items-center justify-center flex-shrink-0">
          <Medal className="w-5 h-5 text-white" />
        </div>
        <div>
          <p className="font-bold text-sm">Veteran Support</p>
          <p className="text-white/80 text-xs">Resources & crisis help — always available</p>
        </div>
      </div>
      <div className="space-y-2">
        <a
          href="https://www.veteranscrisisline.net"
          target="_blank"
          rel="noopener noreferrer"
          className="flex items-center justify-between p-3 bg-white/15 hover:bg-white/25 rounded-xl transition-colors"
        >
          <div>
            <p className="font-bold text-sm">Veterans Crisis Line</p>
            <p className="text-white/70 text-xs">Call/text 988, press 1 — 24/7 confidential</p>
          </div>
          <Phone className="w-4 h-4 text-white/70" />
        </a>
        <a
          href="https://www.mentalhealth.va.gov"
          target="_blank"
          rel="noopener noreferrer"
          className="flex items-center justify-between p-3 bg-white/15 hover:bg-white/25 rounded-xl transition-colors"
        >
          <div>
            <p className="font-bold text-sm">VA Mental Health</p>
            <p className="text-white/70 text-xs">Free care for eligible veterans — mentalhealth.va.gov</p>
          </div>
          <ExternalLink className="w-4 h-4 text-white/70" />
        </a>
        <a
          href="https://www.mightyoaksprograms.org"
          target="_blank"
          rel="noopener noreferrer"
          className="flex items-center justify-between p-3 bg-white/15 hover:bg-white/25 rounded-xl transition-colors"
        >
          <div>
            <p className="font-bold text-sm">Mighty Oaks Foundation</p>
            <p className="text-white/70 text-xs">Christ-centered warrior healing programs</p>
          </div>
          <ExternalLink className="w-4 h-4 text-white/70" />
        </a>
      </div>
      <p className="text-white/60 text-xs mt-3 text-center">
        Expand the Veteran Transition & Freedom section for guidance, resources, and local support.
      </p>
    </div>
  )
}

/* ─── The Faith Connection Card ─────────────────────────────────── */
function FaithConnectionCard() {
  return (
    <div className="rounded-2xl overflow-hidden shadow-md border border-primary-100">
      {/* Header band */}
      <div className="bg-gradient-to-r from-primary-500 to-primary-400 px-5 py-4 flex items-center gap-3">
        <div className="w-10 h-10 bg-white/20 rounded-xl flex items-center justify-center flex-shrink-0">
          <Heart className="w-5 h-5 text-white" />
        </div>
        <div>
          <p className="text-white font-extrabold text-base leading-tight">The Faith Connection</p>
          <p className="text-white/80 text-xs">Serving God's people — completely free</p>
        </div>
      </div>

      {/* Body */}
      <div className="bg-white px-5 py-4 space-y-3">
        <p className="text-gray-700 text-sm leading-relaxed">
          Every course, resource, and tool in this app is provided at <strong className="text-primary-600">no cost</strong> through
          the generosity of The Faith Connection ministry. Your donation helps us keep this guidance
          free for everyone who needs it — no matter where they are in their journey.
        </p>

        {/* Donate CTA */}
        <a
          href="https://www.thefaithconnection.org"
          target="_blank"
          rel="noopener noreferrer"
          className="flex items-center justify-center gap-2 w-full bg-primary-500 hover:bg-primary-600 active:bg-primary-700 text-white font-bold py-3 rounded-xl transition-colors shadow-sm"
        >
          <Heart className="w-4 h-4" />
          Donate &amp; Keep Services Free
          <ExternalLink className="w-4 h-4 opacity-80" />
        </a>

        {/* Secondary link */}
        <a
          href="https://www.thefaithconnection.org"
          target="_blank"
          rel="noopener noreferrer"
          className="flex items-center justify-center gap-1.5 text-primary-500 hover:text-primary-700 text-sm font-semibold transition-colors"
        >
          <Globe className="w-4 h-4" />
          Visit thefaithconnection.org
          <ExternalLink className="w-3.5 h-3.5 opacity-70" />
        </a>
      </div>
    </div>
  )
}
