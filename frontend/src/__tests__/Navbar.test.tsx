import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import Navbar from '../components/common/Navbar'
import { BrowserRouter } from 'react-router-dom'

describe('Navbar', () => {
  it('renders the platform title', () => {
    render(
      <BrowserRouter>
        <Navbar />
      </BrowserRouter>
    )
    expect(screen.getByText('QoS Multi-Cloud Platform')).toBeInTheDocument()
  })
})
