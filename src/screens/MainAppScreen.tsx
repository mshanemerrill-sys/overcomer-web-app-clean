import { useState } from 'react'
import { Chrome as Home, Quote, MessageCircle, BookOpen, ScrollText, TriangleAlert as AlertTriangle, User, Settings } from 'lucide-react'
import { useAppStore } from '../store/useAppStore'
import FreedomTab from './tabs/FreedomTab'
import InspirationTab from './tabs/InspirationTab'
import CompanionTab from './tabs/CompanionTab'
import JournalTab from './tabs/JournalTab'
import BibleTab from './tabs/BibleTab'
import SOSOverlay from '../components/SOSOverlay'
import type { FocusPath } from '../lib/types'

type TabType = 'freedom' | 'inspiration' | 'companion' | 'journal' | 'bible'

interface MainAppScreenProps {
  onShowAuth: () => void
  onShowApiSettings: () => void
}

export default function MainAppScreen({ onShowAuth, onShowApiSettings }: MainAppScreenProps) {
  const [activeTab, setActiveTab] = useState<TabType>('freedom')
  const [showSOS, setShowSOS] = useState(false)
  const { userPath, setUserPath, user, customApiKey } = useAppStore()

  const getPathLabel = (path: FocusPath | null) => {
    switch (path) {
      case 'SUBSTANCE_RECOVERY': return 'Recovery'
      case 'MENTAL_HEALTH': return 'Wellness'
      case 'TOUGH_DAY': return 'Tough Day'
      case 'TESTIMONY_VICTORY': return 'Victory'
      case 'VETERAN_TRANSITION': return 'Veteran'
      case 'REENTRY_RESTORATION': return 'Restoration'
      default: return 'Select'
    }
  }

  return (
    <div className="h-[100dvh] bg-background-light flex flex-col overflow-hidden">
      {/* Top Bar */}
      <header className="bg-white shadow-sm sticky top-0 z-40 safe-top">
        <div className="flex items-center justify-between px-4 py-3">
          {/* SOS Button */}
          <button
            onClick={() => setShowSOS(true)}
            className="flex items-center gap-1 bg-red-600 hover:bg-red-700 text-white px-3 py-2 rounded-xl font-extrabold text-sm transition-colors"
          >
            <AlertTriangle className="w-4 h-4" />
            SOS
          </button>

          {/* Title */}
          <h1 className="text-lg font-extrabold text-primary-500 tracking-wide">
            OverComer
          </h1>

          {/* Right Actions */}
          <div className="flex items-center gap-2">
            <button
              onClick={() => setUserPath(null)}
              className="px-3 py-1.5 bg-primary-100 text-primary-600 rounded-full text-xs font-bold hover:bg-primary-200 transition-colors"
            >
              {getPathLabel(userPath)}
            </button>

            <div className="relative">
              <button
                onClick={onShowAuth}
                className="p-2 rounded-full hover:bg-gray-100 transition-colors"
              >
                <User className={`w-6 h-6 ${user ? 'text-primary-500' : 'text-gray-400'}`} />
                {user && (
                  <span className="absolute top-1 right-1 w-2.5 h-2.5 bg-green-500 rounded-full border-2 border-white" />
                )}
              </button>
            </div>

            <button
              onClick={onShowApiSettings}
              className="p-2 rounded-full hover:bg-gray-100 transition-colors relative"
            >
              <Settings className={`w-6 h-6 ${customApiKey ? 'text-green-500' : 'text-gray-400'}`} />
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 min-h-0 overflow-y-auto">
        {activeTab === 'freedom' && <FreedomTab onNavigateToCompanion={() => setActiveTab('companion')} />}
        {activeTab === 'inspiration' && <InspirationTab onNavigateToCompanion={() => setActiveTab('companion')} />}
        {activeTab === 'companion' && <CompanionTab />}
        {activeTab === 'journal' && <JournalTab />}
        {activeTab === 'bible' && <BibleTab />}
      </main>

      {/* Bottom Navigation */}
      <nav className="bg-white border-t border-gray-200 safe-bottom sticky bottom-0">
        <div className="grid grid-cols-5 py-1.5">
          <NavButton
            icon={<Home className="w-5 h-5" />}
            label="Freedom"
            active={activeTab === 'freedom'}
            onClick={() => setActiveTab('freedom')}
          />
          <NavButton
            icon={<Quote className="w-5 h-5" />}
            label="Inspire"
            active={activeTab === 'inspiration'}
            onClick={() => setActiveTab('inspiration')}
          />
          <NavButton
            icon={<MessageCircle className="w-5 h-5" />}
            label={["Overcomer's", "Companion"]}
            active={activeTab === 'companion'}
            onClick={() => setActiveTab('companion')}
          />
          <NavButton
            icon={<ScrollText className="w-5 h-5" />}
            label="Logs"
            active={activeTab === 'journal'}
            onClick={() => setActiveTab('journal')}
          />
          <NavButton
            icon={<BookOpen className="w-5 h-5" />}
            label="Bible"
            active={activeTab === 'bible'}
            onClick={() => setActiveTab('bible')}
          />
        </div>
      </nav>

      {/* SOS Overlay */}
      {showSOS && <SOSOverlay onClose={() => setShowSOS(false)} />}
    </div>
  )
}

function NavButton({
  icon,
  label,
  active,
  onClick
}: {
  icon: React.ReactNode
  label: string | string[]
  active: boolean
  onClick: () => void
}) {
  return (
    <button
      onClick={onClick}
      className={`flex flex-col items-center justify-center py-2 px-0.5 transition-colors ${
        active
          ? 'text-primary-500'
          : 'text-gray-400 hover:text-gray-600'
      }`}
    >
      {icon}
      {Array.isArray(label) ? (
        <span className={`flex flex-col items-center mt-0.5 leading-tight ${active ? 'font-bold' : 'font-medium'}`}>
          {label.map((line, i) => (
            <span key={i} className="text-[10px] block text-center">{line}</span>
          ))}
        </span>
      ) : (
        <span className={`text-xs mt-1 font-medium ${active ? 'font-bold' : ''}`}>
          {label}
        </span>
      )}
    </button>
  )
}
