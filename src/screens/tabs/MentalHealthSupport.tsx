import { Heart, BookOpen, ExternalLink } from 'lucide-react'

const guides = [
  {
    name: 'Dr. Gary R. Collins — Christian Counseling',
    description: 'A comprehensive clinical-spiritual framework for anxiety, depression, and personal crisis management.'
  },
  {
    name: 'Dr. Timothy Clinton & AACC — Competent Christian Counseling',
    description: 'Clinically competent, Scripture-grounded approaches for renewing the mind and processing deep trauma.'
  },
  {
    name: 'Dr. Larry Crabb — Connecting & Understanding People',
    description: 'A relational healing model that addresses core longings and finding complete security in God.'
  }
]

export default function MentalHealthSupport() {
  return (
    <div className="p-4 space-y-4">
      <div className="rounded-2xl border border-primary-100 bg-primary-50 p-4">
        <div className="flex items-center gap-2 text-primary-700">
          <Heart className="h-5 w-5" />
          <h3 className="text-xs font-extrabold uppercase tracking-[0.14em]">Vetted Biblical Clinical Counseling</h3>
        </div>
        <p className="mt-3 text-sm leading-relaxed text-gray-700">
          We align spiritual healing with healthy mind alignment under Christ, drawing from leading biblical and clinical counseling experts. This app does not promote or use medication-assisted treatment or chemical dependencies for soul care.
        </p>
      </div>

      <div>
        <h4 className="flex items-center gap-2 font-bold text-gray-900">
          <BookOpen className="h-4 w-4 text-primary-500" />
          Trusted Counseling Guides &amp; Literature
        </h4>
        <div className="mt-3 space-y-3">
          {guides.map(guide => (
            <div key={guide.name} className="rounded-xl border border-gray-100 bg-white p-3 shadow-sm">
              <p className="text-sm font-bold text-gray-900">{guide.name}</p>
              <p className="mt-1 text-xs leading-relaxed text-gray-600">{guide.description}</p>
            </div>
          ))}
        </div>
      </div>

      <a
        href="https://www.aacc.net"
        target="_blank"
        rel="noopener noreferrer"
        className="flex items-center justify-center gap-2 rounded-xl bg-primary-500 px-4 py-3 text-sm font-bold text-white"
      >
        Find Christian Counseling Resources
        <ExternalLink className="h-4 w-4" />
      </a>
    </div>
  )
}
