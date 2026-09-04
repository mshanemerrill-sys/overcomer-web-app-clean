import { type FocusPath } from '../lib/types'
import { Cloud, Shield, Heart, Star, ChevronRight, RefreshCw } from 'lucide-react'
import { useAppStore } from '../store/useAppStore'
import { useEffect } from 'react'

interface FocusSelectionScreenProps {
  onSelect: (path: FocusPath) => void
}

export default function FocusSelectionScreen({ onSelect }: FocusSelectionScreenProps) {
  const user = useAppStore(state => state.user)

  useEffect(() => {
    document.title = 'OverComer - Welcome'
  }, [])

  return (
    <div className="min-h-screen bg-[#FEF7FF] px-6 py-8 flex flex-col items-center">
      <div className="w-full max-w-[500px] animate-fade-in">
        <BannerHeader />

        <div className="mt-6 mb-5 text-center">
          {user?.displayName && (
            <p className="text-sm font-semibold text-primary-500 mb-2">Welcome back, {user.displayName}</p>
          )}
          <h1 className="text-xl leading-7 font-extrabold text-[#1D1B20] mb-4">
            Welcome to Overcomer—where we live from Christ’s position of Victory
          </h1>
          <div className="bg-[#E7E0EC]/50 rounded-2xl p-5">
            <p className="text-[#49454F] text-sm leading-[22px]">
              This App is built to lift up those fighting addiction or mental health struggles, assist veterans processing service, or support individuals overcoming the weight of past incarceration. It is equally a refuge for anyone who doesn't face these specific battles but is simply having a rough day and needs a lift. Out of every struggle comes a story.
              <br /><br />
              Step into your focus path, claim your peace, or simply log in to share your testimony and Victory Day.
            </p>
          </div>

          <button
            onClick={() => onSelect('TESTIMONY_VICTORY')}
            className="mt-5 w-full flex items-center gap-3 rounded-2xl border-[1.5px] border-[#FFA000] bg-[#FFF8E1] p-4 text-left shadow-sm active:scale-[0.99]"
          >
            <span className="w-10 h-10 rounded-full bg-[#FFA000]/15 flex items-center justify-center flex-shrink-0">
              <Star className="w-6 h-6 text-[#FFA000]" />
            </span>
            <span className="flex-1 text-sm font-bold text-[#E65100]">
              Tap here to view testimonies of other OverComers Here
            </span>
            <ChevronRight className="w-5 h-5 text-[#FFA000]" />
          </button>

          <p className="text-[#625B71] text-sm mt-5">
            Select a focus path below to customize your experience:
          </p>
        </div>

        <div className="space-y-4">
          <FocusOptionCard
            title="All Around Tough Day"
            description="Direct encouragement, simplified calming breathing, and a compassionate space to vent your stress instantly."
            icon={<Cloud className="w-6 h-6" />}
            color="bg-red-500"
            onClick={() => onSelect('TOUGH_DAY')}
          />

          <FocusOptionCard
            title="Substance Recovery"
            description="Our core Christian choice-based freedom theology, sobriety tracker, and relapse prevention thought reframing logs."
            icon={<Shield className="w-6 h-6" />}
            color="bg-primary-400"
            onClick={() => onSelect('SUBSTANCE_RECOVERY')}
          />

          <FocusOptionCard
            title="Mental Health Wellness"
            description="Peace scriptures, anxiety logs, emotional distress resources, and beautiful breath exercises."
            icon={<Heart className="w-6 h-6" />}
            color="bg-accent-teal"
            onClick={() => onSelect('MENTAL_HEALTH')}
          />

          <FocusOptionCard
            title="The Next Mission: Veteran Support & Wellness"
            description="You were trained to endure the hardest battles, but you don't have to fight the invisible ones alone. Whether you are navigating the transition to civilian life, processing the weight of your service, or simply having a tough day, find faith-based tools and a community that understands. Seeking support isn't stepping down—it's stepping up. Click here for specialized veteran resources and daily strength."
            icon={<Star className="w-6 h-6" />}
            color="bg-[#1B5E20]"
            onClick={() => onSelect('VETERAN_TRANSITION')}
          />

          <FocusOptionCard
            title="Steps to Restoration: Life and Leadership After Incarceration"
            description="Your past does not dictate your destiny; Christ does. Moving forward requires strength, patience, and a strong support network. This space provides daily encouragement, targeted scripture, and a brotherhood of believers who understand the road you are walking. Your story isn't over—it’s just getting started. Click here to discover your value, your worth, and your next steps toward victory."
            icon={<RefreshCw className="w-6 h-6" />}
            color="bg-[#3F51B5]"
            onClick={() => onSelect('REENTRY_RESTORATION')}
          />

          <FocusOptionCard
            title="Today is a Testimony/Victory Day"
            description="Celebrate what God has done! Enjoy victorious scriptures, battle-winning quotes, and share your triumphs with your OverComer companion."
            icon={<Star className="w-6 h-6" />}
            color="bg-[#FFA000]"
            onClick={() => onSelect('TESTIMONY_VICTORY')}
          />
        </div>
      </div>
    </div>
  )
}

function FocusOptionCard({
  title,
  description,
  icon,
  color,
  onClick
}: {
  title: string
  description: string
  icon: React.ReactNode
  color: string
  onClick: () => void
}) {
  return (
    <button
      onClick={onClick}
      className="w-full bg-white rounded-2xl p-4 shadow-sm border border-[#CAC4D0]/70 hover:shadow-md transition-all duration-200 flex items-center gap-4 group active:scale-[0.99]"
    >
      <div className="w-12 h-12 bg-[#F3EDF7] rounded-full flex items-center justify-center">
        <span className={`${color} text-white rounded-full p-1.5`}>
        {icon}
        </span>
      </div>
      <div className="flex-1 text-left">
        <h3 className="font-bold text-[#1D1B20]">{title}</h3>
        <p className="text-sm text-[#49454F] mt-1">{description}</p>
      </div>
      <ChevronRight className="w-5 h-5 text-gray-400 group-hover:text-gray-600 transition-colors" />
    </button>
  )
}

function BannerHeader() {
  return (
    <div className="relative bg-gradient-to-b from-primary-800 via-primary-600 to-primary-400 rounded-3xl overflow-hidden shadow-md h-[200px]">
      {/* Sunset sky gradient background */}
      <div className="absolute inset-0 bg-gradient-to-b from-[#2E1C47] via-[#6B2D5C] via-60% to-[#D84A5A] to-[#FF9E79]" />

      {/* Sun */}
      <div className="absolute bottom-12 left-1/2 -translate-x-1/2 w-24 h-24 rounded-full bg-yellow-100/80 shadow-lg shadow-yellow-200/50" />
      <div className="absolute bottom-10 left-1/2 -translate-x-1/2 w-32 h-32 rounded-full bg-yellow-400/20 blur-sm" />

      {/* Ground silhouette */}
      <div className="absolute bottom-0 left-0 right-0 h-14 bg-[#160E22]" style={{ borderRadius: '50% 50% 0 0' }} />

      {/* Person silhouette - standing in victory pose */}
      <svg className="absolute bottom-14 left-1/2 -translate-x-1/2" width="80" height="120" viewBox="0 0 80 120">
        {/* Head */}
        <circle cx="40" cy="15" r="10" fill="#160E22" />
        <circle cx="40" cy="15" r="12" fill="none" stroke="#160E22" strokeWidth="3" />

        {/* Body */}
        <path d="M28 30 L52 30 L48 65 L32 65 Z" fill="#160E22" />

        {/* Arms spread wide */}
        <path d="M28 35 L5 25 L3 30 L25 40 Z" fill="#160E22" />
        <path d="M52 35 L75 25 L77 30 L55 40 Z" fill="#160E22" />

        {/* Legs */}
        <path d="M32 65 L38 65 L36 100 L30 100 Z" fill="#160E22" />
        <path d="M42 65 L48 65 L50 100 L44 100 Z" fill="#160E22" />
      </svg>

      {/* Text overlay */}
      <div className="absolute top-6 left-0 right-0 text-center">
        <h2 className="text-2xl font-black text-white text-shadow-lg">I AM a OverComer</h2>
        <p className="text-sm font-semibold italic text-white/90 mt-1 text-shadow-sm">Revelation 12:11</p>
      </div>
    </div>
  )
}
