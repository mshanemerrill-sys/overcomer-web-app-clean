import { useState } from 'react'
import { lookupScripture } from '../../lib/geminiClient'
import { useAppStore } from '../../store/useAppStore'
import { versionedBiblePassages, bibleVersionLabels } from '../../lib/bibleData'
import type { BibleVersion } from '../../lib/bibleData'
import { BookOpen, Search, Shield, Loader as Loader2, ChevronRight, ExternalLink, FileText, Download, BookMarked } from 'lucide-react'
import type { ScriptureResult } from '../../lib/types'

type BibleSubTab = 'reader' | 'study' | 'creed' | 'philosophy'

export default function BibleTab() {
  const [activeSubTab, setActiveSubTab] = useState<BibleSubTab>('reader')
  const { bibleVersion, setBibleVersion } = useAppStore()

  return (
    <div className="flex flex-col h-full">
      {/* Sub-tabs */}
      <div className="bg-white border-b border-gray-200 px-4 pt-4">
        <div className="flex gap-1 bg-gray-100 p-1 rounded-xl overflow-x-auto scrollbar-hide">
          {[
            { key: 'reader', label: 'Bible Reader', icon: <BookOpen className="w-4 h-4" /> },
            { key: 'study', label: 'AI Study Guide', icon: <Search className="w-4 h-4" /> },
            { key: 'creed', label: 'Creed & Shield', icon: <Shield className="w-4 h-4" /> },
            { key: 'philosophy', label: 'Philosophy', icon: <FileText className="w-4 h-4" /> }
          ].map(tab => (
            <button
              key={tab.key}
              onClick={() => setActiveSubTab(tab.key as BibleSubTab)}
              className={`flex-shrink-0 flex items-center justify-center gap-1.5 py-2.5 px-3 rounded-lg font-medium text-sm transition-colors ${
                activeSubTab === tab.key
                  ? 'bg-white text-gray-900 shadow-sm'
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              {tab.icon}
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-y-auto p-4 pb-8">
        {activeSubTab === 'reader' && <BibleReader version={bibleVersion} onVersionChange={setBibleVersion} />}
        {activeSubTab === 'study' && <AIStudyGuide />}
        {activeSubTab === 'creed' && <CreedAndShield />}
        {activeSubTab === 'philosophy' && <PhilosophyResources />}
      </div>
    </div>
  )
}

const ALL_VERSIONS = Object.keys(bibleVersionLabels) as BibleVersion[]

function BibleReader({ version, onVersionChange }: { version: BibleVersion; onVersionChange: (v: BibleVersion) => void }) {
  const [selectedPassage, setSelectedPassage] = useState<typeof versionedBiblePassages[0] | null>(null)

  return (
    <div>
      {/* Version Picker — always visible */}
      <div className="mb-4">
        <div className="flex items-center gap-2 mb-2">
          <BookMarked className="w-4 h-4 text-primary-500" />
          <span className="text-sm font-semibold text-gray-700">Bible Version</span>
        </div>
        <div className="flex gap-2 overflow-x-auto pb-1 scrollbar-hide">
          {ALL_VERSIONS.map(v => (
            <button
              key={v}
              onClick={() => onVersionChange(v)}
              className={`flex-shrink-0 px-4 py-2 rounded-full font-semibold text-sm transition-colors ${
                version === v
                  ? 'bg-primary-500 text-white shadow-sm'
                  : 'bg-white border border-gray-200 text-gray-600 hover:border-primary-300 hover:text-primary-600'
              }`}
            >
              {bibleVersionLabels[v]}
            </button>
          ))}
        </div>
      </div>

      {!selectedPassage ? (
        <>
          <h2 className="text-xl font-bold text-gray-900 mb-4">Select a Passage</h2>
          <div className="space-y-3">
            {versionedBiblePassages.map((passage, index) => (
              <button
                key={index}
                onClick={() => setSelectedPassage(passage)}
                className="w-full bg-white rounded-2xl p-4 shadow-md border border-gray-100 text-left hover:shadow-lg transition-all"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="font-bold text-gray-900">{passage.book} {passage.chapter}</h3>
                    <p className="text-sm text-gray-500">{passage.title}</p>
                    <div className="flex flex-wrap gap-1 mt-2">
                      {passage.themes.map((theme, i) => (
                        <span
                          key={i}
                          className="text-xs bg-primary-100 text-primary-600 px-2 py-0.5 rounded-full"
                        >
                          {theme}
                        </span>
                      ))}
                    </div>
                  </div>
                  <ChevronRight className="w-5 h-5 text-gray-400 flex-shrink-0" />
                </div>
              </button>
            ))}
          </div>
        </>
      ) : (
        <div>
          <button
            onClick={() => setSelectedPassage(null)}
            className="flex items-center gap-1 text-primary-500 font-medium text-sm mb-4"
          >
            <ChevronRight className="w-4 h-4 rotate-180" />
            Back to passages
          </button>

          <div className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
            <div className="bg-gradient-to-r from-primary-400 to-primary-600 p-4 text-white">
              <h2 className="text-xl font-bold">{selectedPassage.book} {selectedPassage.chapter}</h2>
              <p className="text-sm text-white/80">{selectedPassage.title}</p>
              <span className="inline-block mt-2 bg-white/20 text-white text-xs font-bold px-2.5 py-1 rounded-full">
                {bibleVersionLabels[version]}
              </span>
            </div>

            <div className="p-5 space-y-4">
              {selectedPassage.versions[version].map(verse => (
                <p key={verse.number} className="text-gray-700 leading-relaxed">
                  <sup className="text-primary-500 font-semibold text-sm">{verse.number}</sup>{' '}
                  {verse.text}
                </p>
              ))}
            </div>

            {/* Version switcher inside passage */}
            <div className="px-5 pb-5 border-t border-gray-100 pt-4">
              <p className="text-xs text-gray-500 mb-2 font-medium">Read in another version:</p>
              <div className="flex gap-2 flex-wrap">
                {ALL_VERSIONS.map(v => (
                  <button
                    key={v}
                    onClick={() => onVersionChange(v)}
                    className={`px-3 py-1.5 rounded-full font-semibold text-xs transition-colors ${
                      version === v
                        ? 'bg-primary-500 text-white'
                        : 'bg-gray-100 text-gray-600 hover:bg-primary-100 hover:text-primary-600'
                    }`}
                  >
                    {bibleVersionLabels[v]}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

function AIStudyGuide() {
  const [searchInput, setSearchInput] = useState('')
  const [result, setResult] = useState<ScriptureResult | null>(null)
  const [isLoading, setIsLoading] = useState(false)

  const handleSearch = async () => {
    if (!searchInput.trim()) return
    setIsLoading(true)
    try {
      const res = await lookupScripture(searchInput.trim())
      setResult(res)
    } catch {
      setResult({
        reference: searchInput,
        text: 'Failed to load scripture.',
        explanation: 'Please check your connection and try again.'
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div>
      <h2 className="text-xl font-bold text-gray-900 mb-4">AI Study Guide</h2>

      <div className="bg-white rounded-2xl shadow-md border border-gray-100 p-4 mb-4">
        <div className="flex gap-2">
          <input
            type="text"
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            placeholder="Enter scripture reference (e.g., John 8:36)"
            className="flex-1 input-field"
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          />
          <button
            onClick={handleSearch}
            disabled={isLoading || !searchInput.trim()}
            className="bg-primary-400 hover:bg-primary-500 disabled:bg-gray-200 text-white p-3 rounded-xl transition-colors"
          >
            {isLoading ? (
              <Loader2 className="w-5 h-5 animate-spin" />
            ) : (
              <Search className="w-5 h-5" />
            )}
          </button>
        </div>
      </div>

      {result && (
        <div className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
          <div className="p-4 bg-primary-50 border-b border-primary-100">
            <h3 className="font-bold text-primary-700">{result.reference}</h3>
          </div>
          <div className="p-4">
            <p className="text-gray-800 italic text-lg leading-relaxed mb-4">
              "{result.text}"
            </p>
            <div className="bg-accent-teal/10 rounded-xl p-4">
              <h4 className="font-semibold text-accent-teal mb-2">OverComer Study Notes</h4>
              <p className="text-gray-700 leading-relaxed">{result.explanation}</p>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

function CreedAndShield() {
  return (
    <div className="space-y-6">
      {/* The Creed */}
      <div className="bg-gradient-to-br from-primary-500 to-primary-700 rounded-2xl p-6 text-white shadow-lg">
        <h2 className="text-xl font-bold mb-4">The OverComer Creed</h2>
        <div className="space-y-4 text-white/90 leading-relaxed">
          <p>
            <strong>I believe</strong> that through Christ's finished work on the cross, I have been set completely free from every chain, every habit, and every bondage.
          </p>
          <p>
            <strong>I am</strong> a NEW creation in Christ Jesus. The old has passed away; the new has come!
          </p>
          <p>
            <strong>I do not fight</strong> FOR victory—I fight FROM victory. I am already more than a conqueror through Him who loved me.
          </p>
          <p>
            <strong>I reject</strong> the label "addict" or "recovering." I am an OVERCOMER, walking in the fullness of my inheritance in Christ.
          </p>
          <p>
            <strong>When I stumble,</strong> I receive His grace. I confess quickly, repent fully, and move forward in His unconditional love.
          </p>
        </div>
      </div>

      {/* The Shield - Theological Foundation */}
      <div className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
        <div className="p-4 bg-gray-900 text-white">
          <h3 className="font-bold flex items-center gap-2">
            <Shield className="w-5 h-5" />
            The Shield: Our Theological Foundation
          </h3>
        </div>
        <div className="p-5 space-y-4">
          <div className="border-l-4 border-primary-400 pl-4">
            <h4 className="font-semibold text-gray-900">Choice is the Root; Dependence is the Fruit</h4>
            <p className="text-sm text-gray-600 mt-1">
              Addiction manifests through choices, not destiny. Genetics and environment are vulnerabilities, not life sentences. In Christ, you have the power to choose.
            </p>
          </div>

          <div className="border-l-4 border-accent-teal pl-4">
            <h4 className="font-semibold text-gray-900">Complete Freedom in Christ</h4>
            <p className="text-sm text-gray-600 mt-1">
              "So if the Son sets you free, you will be free indeed." (John 8:36) Freedom is not a process—it's a promise fulfilled in Christ.
            </p>
          </div>

          <div className="border-l-4 border-accent-gold pl-4">
            <h4 className="font-semibold text-gray-900">New Creation Identity</h4>
            <p className="text-sm text-gray-600 mt-1">
              "Therefore, if anyone is in Christ, the new creation has come: The old has gone, the new is here!" (2 Corinthians 5:17)
            </p>
          </div>

          <div className="border-l-4 border-accent-coral pl-4">
            <h4 className="font-semibold text-gray-900">Grace When We Fall</h4>
            <p className="text-sm text-gray-600 mt-1">
              "If we confess our sins, He is faithful and just and will forgive us our sins and purify us from all unrighteousness." (1 John 1:9)
            </p>
          </div>
        </div>
      </div>

      {/* Motto */}
      <div className="text-center py-6 bg-accent-gold/10 rounded-2xl">
        <p className="text-2xl font-black text-gray-900 italic">
          "I AM a OverComer"
        </p>
        <p className="text-sm text-gray-600 mt-2">Revelation 12:11</p>
      </div>

      {/* The Faith Connection */}
      <div className="bg-white rounded-2xl shadow-md border border-gray-100 p-5 text-center">
        <h3 className="font-bold text-gray-900 mb-2">A Ministry of</h3>
        <a
          href="https://www.thefaithconnection.org"
          target="_blank"
          rel="noopener noreferrer"
          className="inline-flex items-center gap-2 text-primary-500 font-semibold hover:text-primary-600 transition-colors"
        >
          The Faith Connection
          <ExternalLink className="w-4 h-4" />
        </a>
        <p className="text-sm text-gray-500 mt-2">www.thefaithconnection.org</p>
      </div>
    </div>
  )
}

interface PhilosophyResource {
  title: string
  description: string
  pdf: string
  category: 'theology' | 'devotional' | 'testimony' | 'resources'
}

const philosophyResources: PhilosophyResource[] = [
  {
    title: 'Is Addiction a Disease or a Choice?',
    description: 'A biblical examination of the disease model versus personal responsibility — foundational to the OverComer framework.',
    pdf: '/philosophy/Is_addiction_a_disease_or_a_choice_.pdf',
    category: 'theology'
  },
  {
    title: 'The Ultimate Danger of the Disease Model',
    description: 'How removing personal accountability through the disease model undermines genuine transformation in Christ.',
    pdf: '/philosophy/The_ultimate_danger_of_the_disease_model-_the_total_removal_of_personal_accountability.pdf',
    category: 'theology'
  },
  {
    title: 'Seek God With All Your Heart',
    description: 'A devotional study on what it truly means to pursue God wholeheartedly in your recovery and daily life.',
    pdf: '/philosophy/Seek_God_With_All_Your_Heart.pdf',
    category: 'devotional'
  },
  {
    title: 'Seek First the Kingdom of God',
    description: 'Deep study on Matthew 6:33 — making God\'s Kingdom your first priority in everything.',
    pdf: '/philosophy/Seek_1st_The_Kingdom_Of_God.pdf',
    category: 'devotional'
  },
  {
    title: 'Tell Your Story: 10 Tips for Sharing Your Testimony',
    description: 'Practical guidance on how to share your testimony effectively to help others and glorify God.',
    pdf: '/philosophy/Tell_Your_Story__10_Tips_for_Sharing_Your_Testimony_With_Others.pdf',
    category: 'testimony'
  },
  {
    title: '3 Effective Ways to Examine Ourselves',
    description: 'A guide to honest self-examination through faith, works, and God\'s perspective — essential for Step 4.',
    pdf: '/philosophy/3_Effective_Ways_To_Examine_Ourselves.pdf',
    category: 'theology'
  },
  {
    title: 'Lessen Shame and Fear by Admitting Your Wrongs',
    description: 'Understanding how confession breaks the shame cycle and opens the door to genuine freedom.',
    pdf: '/philosophy/Lessen_Shame_and_Fear_by_Admitting_Your_Wrongs.pdf',
    category: 'devotional'
  },
  {
    title: 'Repentance Verses',
    description: 'A curated collection of scriptures on repentance, turning to God, and receiving His mercy.',
    pdf: '/philosophy/Repent_Verses.pdf',
    category: 'devotional'
  },
  {
    title: 'Laminin — Held Together by the Cross',
    description: 'The remarkable discovery that the protein holding your body together is shaped like a cross — God\'s design in your cells.',
    pdf: '/philosophy/Laminin.pdf',
    category: 'theology'
  },
  {
    title: 'OverComer Program Overview',
    description: 'The complete overview of the OverComer ministry, its mission, and biblical foundations.',
    pdf: '/philosophy/OverComer.pdf',
    category: 'resources'
  },
  {
    title: '7 Steps — Detailed Guide',
    description: 'In-depth notes and teaching material for all 7 steps of the OverComer Obedience Academy.',
    pdf: '/philosophy/7_Steps_Details_(1st_revision).pdf',
    category: 'resources'
  },
  {
    title: 'Ministry Notes',
    description: 'Additional teaching notes and supplementary material from The Faith Connection ministry.',
    pdf: '/philosophy/Notes.pdf',
    category: 'resources'
  },
  {
    title: 'Website Resources',
    description: 'Curated list of trusted online resources for recovery, faith, and ongoing support.',
    pdf: '/philosophy/Website_Resources.pdf',
    category: 'resources'
  },
  {
    title: 'Additional Web Resources',
    description: 'More helpful websites and online tools to support your OverComer journey.',
    pdf: '/philosophy/Websites_Resources.pdf',
    category: 'resources'
  }
]

const philosophyCategoryInfo: Record<PhilosophyResource['category'], { label: string; color: string }> = {
  theology: { label: 'Theology', color: 'bg-primary-500' },
  devotional: { label: 'Devotional', color: 'bg-accent-teal' },
  testimony: { label: 'Testimony', color: 'bg-accent-gold' },
  resources: { label: 'Resources', color: 'bg-secondary-500' }
}

function PhilosophyResources() {
  const [activeCategory, setActiveCategory] = useState<PhilosophyResource['category'] | 'all'>('all')

  const filtered = activeCategory === 'all'
    ? philosophyResources
    : philosophyResources.filter(r => r.category === activeCategory)

  const categories: Array<PhilosophyResource['category'] | 'all'> = ['all', 'theology', 'devotional', 'testimony', 'resources']

  return (
    <div className="space-y-4">
      <div className="bg-gradient-to-br from-gray-900 to-primary-700 rounded-2xl p-5 text-white shadow-lg">
        <h2 className="text-xl font-bold mb-1">Philosophy & Resources</h2>
        <p className="text-sm text-white/80 leading-relaxed">
          Foundational articles, devotionals, and ministry documents to deepen your understanding of freedom in Christ.
        </p>
      </div>

      {/* Category filter */}
      <div className="flex gap-2 overflow-x-auto pb-1 scrollbar-hide">
        {categories.map(cat => {
          const info = cat === 'all' ? null : philosophyCategoryInfo[cat]
          const isActive = activeCategory === cat
          return (
            <button
              key={cat}
              onClick={() => setActiveCategory(cat)}
              className={`flex-shrink-0 px-3 py-1.5 rounded-full font-semibold text-xs transition-colors capitalize ${
                isActive
                  ? 'bg-gray-900 text-white'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
              }`}
            >
              {cat === 'all' ? 'All Resources' : info?.label}
            </button>
          )
        })}
      </div>

      <div className="space-y-3">
        {filtered.map((resource, i) => {
          const catInfo = philosophyCategoryInfo[resource.category]
          return (
            <div key={i} className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
              <div className="p-4">
                <div className="flex items-start gap-3">
                  <div className={`w-10 h-10 ${catInfo.color} rounded-xl flex items-center justify-center text-white flex-shrink-0`}>
                    <FileText className="w-5 h-5" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between gap-2">
                      <div>
                        <span className={`text-xs font-bold uppercase tracking-wide text-white ${catInfo.color} px-2 py-0.5 rounded-full`}>
                          {catInfo.label}
                        </span>
                        <h4 className="font-bold text-gray-900 text-sm mt-1.5 leading-snug">{resource.title}</h4>
                        <p className="text-xs text-gray-500 mt-1 leading-relaxed">{resource.description}</p>
                      </div>
                    </div>
                  </div>
                </div>
                <a
                  href={resource.pdf}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="mt-3 w-full flex items-center justify-center gap-2 bg-primary-50 hover:bg-primary-100 text-primary-600 font-semibold text-sm py-2.5 rounded-xl transition-colors"
                >
                  <Download className="w-4 h-4" />
                  Open PDF
                </a>
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}
