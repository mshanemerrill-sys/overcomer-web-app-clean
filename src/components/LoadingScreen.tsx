export default function LoadingScreen() {
  return (
    <div className="min-h-screen bg-gradient-to-b from-primary-800 via-primary-600 to-primary-400 flex items-center justify-center">
      <div className="text-center">
        <div className="w-32 h-32 mx-auto mb-6 relative">
          {/* Animated circles */}
          <div className="absolute inset-0 border-4 border-white/20 rounded-full animate-pulse" />
          <div className="absolute inset-4 border-4 border-white/30 rounded-full animate-pulse" style={{ animationDelay: '0.2s' }} />
          <div className="absolute inset-8 bg-white/20 rounded-full animate-pulse" style={{ animationDelay: '0.4s' }} />

          {/* Center icon */}
          <div className="absolute inset-12 bg-white rounded-full flex items-center justify-center">
            <svg viewBox="0 0 24 24" className="w-6 h-6 text-primary-500" fill="currentColor">
              <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" />
            </svg>
          </div>
        </div>

        <h1 className="text-2xl font-black text-white text-shadow-lg">
          OverComer
        </h1>
        <p className="text-white/70 text-sm mt-2">
          Walking in Victory
        </p>
      </div>
    </div>
  )
}
